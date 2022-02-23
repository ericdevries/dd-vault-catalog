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

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import nl.knaw.dans.catalog.api.Tar;
import nl.knaw.dans.catalog.api.TarPart;
import nl.knaw.dans.catalog.api.TransferItem;
import nl.knaw.dans.catalog.core.SolrService;
import nl.knaw.dans.catalog.core.TarService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DropwizardExtensionsSupport.class)
class TarAPIResourceTest {

    private static final TarService tarService = Mockito.mock(TarService.class);
    private static final SolrService solrService = Mockito.mock(SolrService.class);
    private static final ResourceExtension EXT = ResourceExtension.builder()
        .addResource(new TarAPIResource(tarService, solrService))
        .build();

    @AfterEach
    void tearDown() {
        Mockito.reset(tarService);
        Mockito.reset(solrService);
    }

    @Test
    void getTar() {
        var tar = new Tar();
        tar.setTarUuid("123");
        Mockito.when(tarService.get(Mockito.any())).thenReturn(Optional.of(tar));

        var found = EXT.target("/api/tar/1").request().get(Tar.class);
        assertEquals("123", found.getTarUuid());
    }

    @Test
    void getTarNotFound() {
        var tar = new Tar();
        tar.setTarUuid("123");
        Mockito.when(tarService.get(Mockito.eq("123"))).thenReturn(Optional.of(tar));

        var response = EXT.target("/api/tar/1").request().get();
        assertEquals(404, response.getStatusInfo().getStatusCode());
    }

    @Test
    void createTar() {
        var entity = new Tar();
        entity.setTarUuid("123");
        entity.setArchivalDate(LocalDateTime.now());
        entity.setVaultPath("vault-x");

        var transferItem = new TransferItem();
        transferItem.setBagId("bagid");
        transferItem.setDatastation("ds1");
        transferItem.setDataversePid("dspid");
        transferItem.setDataversePidVersion("dspidversion");
        transferItem.setObjectVersion("1.0");
        transferItem.setOcflObjectPath("path/to/thing");
        transferItem.setSwordClient("PAR");
        transferItem.setNbn("nbn:version");

        var part = new TarPart();
        part.setPartName("0000");
        part.setChecksumAlgorithm("md5");
        part.setChecksumValue("thevalue");

        entity.setTarParts(List.of(part));

        entity.setTransferItems(List.of(transferItem));
        var response = EXT.target("/api/tar/").request().post(Entity.json(entity));
        assertEquals(200, response.getStatusInfo().getStatusCode());
    }

    @Test
    void createTarIncomplete() {
        var entity = new Tar();
        entity.setTarUuid("123");
        entity.setArchivalDate(LocalDateTime.now());
        entity.setVaultPath("vault-x");
        var response = EXT.target("/api/tar/").request().post(Entity.json(entity));
        assertEquals(422, response.getStatusInfo().getStatusCode());
    }
}
