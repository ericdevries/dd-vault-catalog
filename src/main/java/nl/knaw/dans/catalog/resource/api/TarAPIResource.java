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
import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.catalog.api.TarParameterDto;
import nl.knaw.dans.catalog.core.UseCases;
import nl.knaw.dans.catalog.core.exception.OcflObjectVersionAlreadyInTarException;
import nl.knaw.dans.catalog.core.exception.OcflObjectVersionNotFoundException;
import nl.knaw.dans.catalog.core.exception.TarAlreadyExistsException;
import nl.knaw.dans.catalog.core.exception.TarNotFoundException;
import nl.knaw.dans.catalog.resource.TarApi;
import nl.knaw.dans.catalog.resource.mappers.TarMapper;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/api/tar")
@Slf4j
public class TarAPIResource implements TarApi {
    private final TarMapper tarMapper = TarMapper.INSTANCE;
    private final UseCases useCases;

    public TarAPIResource(UseCases useCases) {
        this.useCases = useCases;
    }

    @Override
    public Response addArchive(TarParameterDto tarDto) {
        log.info("Received new TAR {}, storing in database", tarDto);

        try {
            var result = useCases.createTar(tarDto.getTarUuid(), tarMapper.convert(tarDto));
            var converted = tarMapper.convert(result);
            return Response.ok(converted).status(201).build();
        }
        catch (OcflObjectVersionAlreadyInTarException | TarAlreadyExistsException e) {
            log.error(e.getMessage(), e);
            throw new WebApplicationException(e.getMessage(), Response.Status.CONFLICT);
        }
        catch (OcflObjectVersionNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        }
        catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response getArchiveById(String id) {
        log.debug("Fetching TAR with id {}", id);
        var result = useCases.findTarById(id)
            .map(tarMapper::convert)
            .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

        return Response.ok(result).build();
    }

    @Override
    public Response updateArchive(String id, TarParameterDto tarDto) {
        log.info("Received existing TAR {}, ID is {}, storing in database", tarDto, id);

        try {
            var result = useCases.updateTar(id, tarMapper.convert(tarDto));
            var response = tarMapper.convert(result);

            return Response.ok(response).build();
        }
        catch (OcflObjectVersionAlreadyInTarException e) {
            log.error(e.getMessage(), e);
            throw new WebApplicationException(e.getMessage(), Response.Status.CONFLICT);
        }
        catch (TarNotFoundException | OcflObjectVersionNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        }
    }

}
