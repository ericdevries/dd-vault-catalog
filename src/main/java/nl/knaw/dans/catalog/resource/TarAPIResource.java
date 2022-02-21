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

package nl.knaw.dans.catalog.resource;

import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.catalog.api.Tar;
import nl.knaw.dans.catalog.core.TarService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/tar")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TarAPIResource {

    private final TarService tarService;

    public TarAPIResource(TarService tarService) {
        this.tarService = tarService;
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Tar get(@PathParam("id") String id) {
        return tarService.get(id)
            .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
    }

    @POST
    @UnitOfWork
    public Tar add(@NotNull @Valid Tar tar) {
        if (tarService.get(tar.getTarUuid()).isPresent()) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        tarService.saveTar(tar);
        return tar;
    }
}
