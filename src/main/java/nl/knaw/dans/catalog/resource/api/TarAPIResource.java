/*
 * Copyright (C) 2022 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.knaw.dans.catalog.resource.api;

import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.catalog.core.OcflObjectVersionService;
import nl.knaw.dans.catalog.core.SolrService;
import nl.knaw.dans.catalog.core.TarService;
import nl.knaw.dans.catalog.core.mapper.TarDtoMapper;
import nl.knaw.dans.catalog.core.mapper.TarMapper;
import nl.knaw.dans.openapi.api.TarDto;
import nl.knaw.dans.openapi.server.TarApi;
import org.apache.solr.client.solrj.SolrServerException;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/api/tar")
public class TarAPIResource implements TarApi {
    private static final Logger log = LoggerFactory.getLogger(TarAPIResource.class);

    private final TarService tarService;
    private final SolrService solrService;
    private final OcflObjectVersionService ocflObjectVersionService;
    private final TarMapper tarMapper = Mappers.getMapper(TarMapper.class);
    private final TarDtoMapper tarDtoMapper = Mappers.getMapper(TarDtoMapper.class);

    public TarAPIResource(TarService tarService, SolrService solrService, OcflObjectVersionService ocflObjectVersionService) {
        this.tarService = tarService;
        this.solrService = solrService;
        this.ocflObjectVersionService = ocflObjectVersionService;
    }

    @Override
    @UnitOfWork
    public TarDto addArchive(TarDto tarDto) {
        log.info("Received new TAR {}, storing in database", tarDto);

        if (tarService.get(tarDto.getTarUuid()).isPresent()) {
            log.error("TAR with ID {} is already present in database", tarDto.getTarUuid());
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        // check that none of these ocfl objects already exist within another TAR
        for (var ocflObject : tarDto.getOcflObjects()) {
            var existingOcflObject = ocflObjectVersionService.findByBagIdAndVersion(ocflObject.getBagId(), ocflObject.getVersionMajor(), ocflObject.getVersionMinor());

            if (existingOcflObject.isPresent()) {
                throw new WebApplicationException(String.format("OCFL object version with id %s already exists as part of another TAR", existingOcflObject.get().getId()));
            }
        }

        return saveAndIndexTar(tarDto);
    }

    @Override
    @UnitOfWork
    public TarDto getArchiveById(String id) {
        log.debug("Fetching TAR with id {}", id);
        return tarService.get(id)
            .map(tarMapper::tarToTarDto)
            .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

    }

    @Override
    @UnitOfWork
    public TarDto updateArchive(String id, TarDto tarDto) {
        log.info("Received existing TAR {}, ID is {}, storing in database", tarDto, id);

        if (!id.equals(tarDto.getTarUuid())) {
            log.warn("ID's are not the same, returning error");
            throw new WebApplicationException(String.format("ID %s does not match tar uuid %s", id, tarDto.getTarUuid()),
                Response.Status.BAD_REQUEST);
        }

        var existingTar = tarService.get(id);

        if (existingTar.isEmpty()) {
            log.warn("Update on non-existing ID {}", id);
            throw new WebApplicationException(String.format("Tar with UUID %s does not exist", id), Response.Status.NOT_FOUND);
        }

        // check that none of these ocfl objects already exist within another TAR
        for (var ocflObject : tarDto.getOcflObjects()) {
            var existingOcflObject = ocflObjectVersionService.findByBagIdAndVersion(ocflObject.getBagId(), ocflObject.getVersionMajor(), ocflObject.getVersionMinor());

            if (existingOcflObject.isPresent() && !existingOcflObject.get().getTar().getTarUuid().equals(id)) {
                throw new WebApplicationException(String.format("OCFL object version with id %s already exists as part of another TAR", existingOcflObject.get().getId()));
            }
        }

        return saveAndIndexTar(tarDto);
    }

    private TarDto saveAndIndexTar(TarDto tarDto) {
        var tar = tarService.saveTar(tarDtoMapper.tarDtoToTar(tarDto));

        try {
            log.info("Updating TAR document in Solr index");
            solrService.indexArchive(tar);
        }
        catch (IOException | SolrServerException e) {
            log.error("Unable to save document in Solr index", e);
        }

        return tarMapper.tarToTarDto(tar);
    }
}
