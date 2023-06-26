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
import nl.knaw.dans.catalog.api.CreateOcflObjectVersionRequestDto;
import nl.knaw.dans.catalog.core.UseCases;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersionId;
import nl.knaw.dans.catalog.core.exception.OcflObjectVersionAlreadyExistsException;
import nl.knaw.dans.catalog.resource.OcflObjectApi;
import nl.knaw.dans.catalog.resource.mappers.OcflObjectVersionMapper;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/api/ocflObject")
@Slf4j
public class OcflObjectApiResource implements OcflObjectApi {
    private final OcflObjectVersionMapper ocflObjectVersionMapper = OcflObjectVersionMapper.INSTANCE;

    private final UseCases useCases;

    public OcflObjectApiResource(UseCases useCases) {
        this.useCases = useCases;
    }

    @Override
    @UnitOfWork
    public Response createOcflObjectVersion(String bagId, Integer versionNumber, CreateOcflObjectVersionRequestDto createOcflObjectVersionRequestDto) {
        try {
            var result = useCases.createOcflObjectVersion(
                new OcflObjectVersionId(bagId, versionNumber),
                ocflObjectVersionMapper.convert(createOcflObjectVersionRequestDto)
            );

            var response = ocflObjectVersionMapper.convert(result);
            return Response.ok(response).status(201).build();
        }
        catch (OcflObjectVersionAlreadyExistsException e) {
            log.error(e.getMessage(), e);
            throw new WebApplicationException(e.getMessage(), Response.Status.CONFLICT);
        }
    }

    @Override
    public Response getOcflObjectByBagIdAndVersionNumber(String bagId, Integer versionNumber) {
        return null;
    }

    @Override
    public Response getOcflObjectsByBagId(String bagId) {
        return null;
    }

    @Override
    public Response getOcflObjectsBySwordToken(String swordToken) {
        return null;
    }
}
