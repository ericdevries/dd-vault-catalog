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

import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import nl.knaw.dans.catalog.DdVaultCatalogApplication;
import nl.knaw.dans.catalog.DdVaultCatalogConfiguration;
import nl.knaw.dans.catalog.api.*;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(DropwizardExtensionsSupport.class)
class OcflObjectApiResourceTest {

    public static final DropwizardTestSupport<DdVaultCatalogConfiguration> SUPPORT =
        new DropwizardTestSupport<>(DdVaultCatalogApplication.class,
            ResourceHelpers.resourceFilePath("debug-etc/test.yml")
        );

    @BeforeAll
    public static void beforeClass() throws Exception {
        SUPPORT.before();
    }

    @AfterAll
    public static void afterClass() {
        SUPPORT.after();
    }

    @Test
    public void createOcflVersion_should_return_201() throws Exception {
        var client = new JerseyClientBuilder().build();
        var entity = new CreateOcflObjectVersionRequestDto()
            .dataSupplier("test")
            .exportTimestamp(OffsetDateTime.now())
            .nbn("someNbn");

        var str = SUPPORT.getObjectMapper().writeValueAsString(entity);

        var bagId = UUID.randomUUID().toString();
        var version = 1;

        try (var response = client.target(
                String.format("http://localhost:%d/api/ocflObject/bagId/%s/version/%s", SUPPORT.getLocalPort(), bagId, version))
            .request()
            .put(Entity.json(str))) {

            assertEquals(201, response.getStatus());

            var dto = response.readEntity(OcflObjectVersionDto.class);

            assertEquals(1, dto.getObjectVersion());
            assertEquals("test", dto.getDataSupplier());
            assertNull(dto.getTarUuid());
        }
    }

    @Test
    public void createOcflVersion_should_return_409_if_version_already_exists() throws Exception {
        var client = new JerseyClientBuilder().build();
        var entity = new CreateOcflObjectVersionRequestDto()
            .dataSupplier("test")
            .nbn("someNbn");

        var str = SUPPORT.getObjectMapper().writeValueAsString(entity);

        var bagId = UUID.randomUUID().toString();
        var version = 1;

        var url = String.format("http://localhost:%d/api/ocflObject/bagId/%s/version/%s", SUPPORT.getLocalPort(), bagId, version);

        try (var response = client.target(url)
            .request()
            .put(Entity.json(str))) {

            assertEquals(201, response.getStatus());

            try (var conflicted = client.target(url).request().put(Entity.json(str))) {
                assertEquals(409, conflicted.getStatus());
            }
        }
    }

    @Test
    public void getOcflVersion_should_return_existing_item_after_unassignment_from_tar() throws Exception {
        var client = new JerseyClientBuilder().build();
        var entity = new CreateOcflObjectVersionRequestDto()
            .dataSupplier("test")
            .nbn("someNbn");

        var str = SUPPORT.getObjectMapper().writeValueAsString(entity);

        var bagId = UUID.randomUUID().toString();
        var version = 1;

        var url = String.format("http://localhost:%d/api/ocflObject/bagId/%s/version/%s", SUPPORT.getLocalPort(), bagId, version);

        // creating ocfl object
        try (var response = client.target(url).request().put(Entity.json(str))) {
            assertEquals(201, response.getStatus());
        }

        var tar = new TarParameterDto()
            .tarUuid(UUID.randomUUID().toString())
            .vaultPath("somePath")
            .archivalDate(OffsetDateTime.now())
            .ocflObjectVersions(List.of(new OcflObjectVersionRefDto().objectVersion(version).bagId(UUID.fromString(bagId))));

        // creating tar
        var apiUrl = String.format("http://localhost:%d/api/tar/", SUPPORT.getLocalPort());
        try (var response = client.target(apiUrl).request().post(Entity.json(tar))) {
            assertEquals(201, response.getStatus());

            var tarResponse = response.readEntity(TarDto.class);
            assertEquals(bagId, tarResponse.getOcflObjectVersions().get(0).getBagId().toString());
        }

        // see if the ocfl object version is bound to the tar
        try (var response = client.target(url).request().get()) {
            assertEquals(200, response.getStatus());

            var ocflResponse = response.readEntity(OcflObjectVersionDto.class);
            assertEquals(tar.getTarUuid(), ocflResponse.getTarUuid().toString());
            assertEquals(bagId, ocflResponse.getBagId().toString());
        }

        // updating tar, but without the ocfl object
        try (var response = client.target(apiUrl + tar.getTarUuid()).request().put(Entity.json(tar.ocflObjectVersions(List.of())))) {
            assertEquals(200, response.getStatus());
            var tarResponse = response.readEntity(TarDto.class);
            assertEquals(0, tarResponse.getOcflObjectVersions().size());
        }

        // now verify the ocfl object version is still there, not bound to a TAR
        try (var response = client.target(url).request().get()) {
            assertEquals(200, response.getStatus());

            var ocflResponse = response.readEntity(OcflObjectVersionDto.class);
            assertNull(ocflResponse.getTarUuid());
            assertEquals(bagId, ocflResponse.getBagId().toString());
        }
    }
}