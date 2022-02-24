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
import nl.knaw.dans.catalog.core.SolrService;
import nl.knaw.dans.catalog.core.TarService;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/api/tar")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TarAPIResource {
    private static final Logger log = LoggerFactory.getLogger(TarAPIResource.class);

    private final TarService tarService;
    private final SolrService solrService;

    public TarAPIResource(TarService tarService, SolrService solrService) {
        this.tarService = tarService;
        this.solrService = solrService;
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Tar get(@PathParam("id") String id) {
        log.debug("Fetching TAR with id {}", id);
        return tarService.get(id).orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
    }

    @POST
    @UnitOfWork
    public Tar add(@NotNull @Valid Tar tar) {
        log.info("Received new TAR {}, storing in database", tar);
        if (tarService.get(tar.getTarUuid()).isPresent()) {
            log.error("TAR with ID {} is already present in database", tar.getTarUuid());
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        tarService.saveTar(tar);

        try {
            log.info("Adding TAR document to Solr index");
            solrService.indexArchive(tar);
        }
        catch (IOException | SolrServerException e) {
            log.error("Unable to save document in Solr index", e);
        }

        return tar;
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Tar update(@PathParam("id") String id, @NotNull @Valid Tar tar) {
        log.info("Received existing TAR {}, ID is {}, storing in database", tar, id);

        if (!id.equals(tar.getTarUuid())) {
            log.warn("ID's are not the same, returning error");
            throw new WebApplicationException(String.format("ID %s does not match tar uuid %s", id, tar.getTarUuid()),
                Response.Status.BAD_REQUEST);
        }

        tarService.saveTar(tar);

        try {
            log.info("Updating TAR document in Solr index");
            solrService.indexArchive(tar);
        }
        catch (IOException | SolrServerException e) {
            log.error("Unable to save document in Solr index", e);
        }

        return tar;
    }
}
