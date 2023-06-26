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

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DropwizardExtensionsSupport.class)
class TarAPIResourceIntegrationTest {

    public static final DropwizardTestSupport<DdVaultCatalogConfiguration> SUPPORT =
        new DropwizardTestSupport<>(DdVaultCatalogApplication.class,
            ResourceHelpers.resourceFilePath("debug-etc/test.yml")
        );

    @BeforeAll
    public static void beforeClass() throws Exception {
        SUPPORT.before();
        SUPPORT.getObjectMapper().registerModule(new JavaTimeModule());
    }

    @AfterAll
    public static void afterClass() {
        SUPPORT.after();
    }

    @Test
    // TODO should we really accept empty tars?
    public void createEmptyTar_should_return_200() throws Exception {
        var client = new JerseyClientBuilder().build();
        var entity = new TarParameterDto()
            .archivalDate(OffsetDateTime.now())
            .vaultPath("test")
            .tarUuid(UUID.randomUUID().toString());

        var str = SUPPORT.getObjectMapper().writeValueAsString(entity);

        try (var response = client.target(
                String.format("http://localhost:%d/api/tar", SUPPORT.getLocalPort()))
            .request()
            .post(Entity.json(str))) {

            assertEquals(201, response.getStatus());
        }
    }

    @Test
    public void createTar_with_tarParts_should_return_200() throws Exception {
        var client = new JerseyClientBuilder().build();

        var part1 = new TarPartParameterDto()
            .partName("part1")
            .checksumAlgorithm("MD5")
            .checksumValue("secret");

        var part2 = new TarPartParameterDto()
            .partName("part2")
            .checksumAlgorithm("MD5")
            .checksumValue("even more secret");

        var entity = new TarParameterDto()
            .archivalDate(OffsetDateTime.now())
            .vaultPath("test")
            .tarUuid(UUID.randomUUID().toString())
            .tarParts(List.of(part1, part2));

        var str = SUPPORT.getObjectMapper().writeValueAsString(entity);

        try (var response = client.target(
                String.format("http://localhost:%d/api/tar", SUPPORT.getLocalPort()))
            .request()
            .post(Entity.json(str))) {

            assertEquals(201, response.getStatus());

            var data = response.readEntity(String.class);
            System.out.println("DATA: " + data);
        }
    }

    @Test
    public void createTar_with_unknown_ocflObjectVersions_should_return_404() throws Exception {
        var client = new JerseyClientBuilder().build();

        var part1 = new TarPartParameterDto()
            .partName("part1")
            .checksumAlgorithm("MD5")
            .checksumValue("secret");

        var ocfl1 = new OcflObjectVersionRefDto()
            .bagId(UUID.randomUUID())
            .objectVersion(1);

        var entity = new TarParameterDto()
            .archivalDate(OffsetDateTime.now())
            .vaultPath("test")
            .tarUuid(UUID.randomUUID().toString())
            .ocflObjectVersions(List.of(ocfl1))
            .tarParts(List.of(part1));

        var str = SUPPORT.getObjectMapper().writeValueAsString(entity);

        try (var response = client.target(
                String.format("http://localhost:%d/api/tar", SUPPORT.getLocalPort()))
            .request()
            .post(Entity.json(str))) {

            assertEquals(404, response.getStatus());
        }
    }

    @Test
    public void createTar_with_ocflObjectVersions_should_return_201() throws Exception {
        var client = new JerseyClientBuilder().build();

        var part1 = new TarPartParameterDto()
            .partName("part1")
            .checksumAlgorithm("MD5")
            .checksumValue("secret");

        var version = createOcflVersion(client, new CreateOcflObjectVersionRequestDto()
            .otherId("random id"));

        var entity = new TarParameterDto()
            .archivalDate(OffsetDateTime.now())
            .vaultPath("test")
            .tarUuid(UUID.randomUUID().toString())
            .ocflObjectVersions(List.of(new OcflObjectVersionRefDto().bagId(version.getBagId()).objectVersion(version.getObjectVersion())))
            .tarParts(List.of(part1));

        var str = SUPPORT.getObjectMapper().writeValueAsString(entity);

        try (var response = client.target(
                String.format("http://localhost:%d/api/tar", SUPPORT.getLocalPort()))
            .request()
            .post(Entity.json(str))) {

            assertEquals(201, response.getStatus());
        }

        var response = client.target(
                String.format("http://localhost:%d/api/tar/%s", SUPPORT.getLocalPort(), entity.getTarUuid()))
            .request()
            .get(String.class);

        System.out.println("RESPONSE: " + response);
//
//        var response = client.target(
//                String.format("http://localhost:%d/api/tar/%s", SUPPORT.getLocalPort(), entity.getTarUuid()))
//            .request()
//            .get(TarDto.class);
//
//        assertEquals(response.getTarUuid(), entity.getTarUuid());
    }
    //    private static final TarService tarService = Mockito.mock(TarService.class);
//    private static final SolrService solrService = Mockito.mock(SolrService.class);
//    private static final OcflObjectVersionService ocflObjectVersionService = Mockito.mock(OcflObjectVersionService.class);
//    private final ResourceExtension EXT = ResourceExtension.builder()
//        .addResource(new TarAPIResource(useCases))
//        .build();
//
//    @AfterEach
//    void tearDown() {
//        Mockito.reset(tarService);
//        Mockito.reset(solrService);
//    }
//
//    @Test
//    void getTar() {
//        var tar = new Tar();
//        tar.setTarUuid("123");
//        Mockito.when(tarService.get(Mockito.any())).thenReturn(Optional.of(tar));
//
//        var found = EXT.target("/api/tar/1").request().get(TarDto.class);
//        assertEquals("123", found.getTarUuid());
//    }
//
//    @Test
//    void getTarNotFound() {
//        var tar = new Tar();
//        tar.setTarUuid("123");
//        Mockito.when(tarService.get(Mockito.eq("123"))).thenReturn(Optional.of(tar));
//
//        var response = EXT.target("/api/tar/1").request().get();
//        assertEquals(404, response.getStatusInfo().getStatusCode());
//    }
//
//    @Test
//    void createTar() throws JsonProcessingException {
//        var entity = new TarDto();
//        entity.setTarUuid("123");
//        entity.setStagedDate(OffsetDateTime.now());
//        entity.setVaultPath("vault-x");
//
//        var ocflObjectDto = new OcflObjectDto();
//        ocflObjectDto.setBagId("bagid");
//        ocflObjectDto.setDatastation("ds1");
//        ocflObjectDto.setDataversePid("dspid");
//        ocflObjectDto.setDataversePidVersion("dspidversion");
//        ocflObjectDto.setObjectVersion("1.0");
//        ocflObjectDto.setVersionMajor(1);
//        ocflObjectDto.setVersionMinor(5);
//        ocflObjectDto.setOcflObjectPath("path/to/thing");
//        ocflObjectDto.setSwordClient("PAR");
//        ocflObjectDto.setNbn("nbn:version");
//        ocflObjectDto.setMetadata(new ObjectMapper().writeValueAsString(Map.of("data1", "5", "data2", "6")));
//        ocflObjectDto.setExportTimestamp(OffsetDateTime.now());
//
//        var part = new TarPartDto();
//        part.setPartName("0000");
//        part.setChecksumAlgorithm("md5");
//        part.setChecksumValue("thevalue");
//
//        entity.setTarParts(List.of(part));
//        entity.setOcflObjects(List.of(ocflObjectDto));
//
//        var result = new Tar();
//        result.setOcflObjectVersions(new ArrayList<>());
//        var ti = new OcflObjectVersion();
//        ti.setMetadata("{\"key\": 1}");
//        result.getOcflObjectVersions().add(ti);
//
//        Mockito.when(tarService.saveTar(Mockito.any())).thenReturn(result);
//
//        var response = EXT.target("/api/tar/").request().post(Entity.json(entity));
//        assertEquals(200, response.getStatusInfo().getStatusCode());
//        var stream = (ByteArrayInputStream) response.getEntity();
//    }
//
//
//    @Test
//    void updateTar() throws JsonProcessingException {
//        var entity = new TarDto();
//        entity.setTarUuid("123");
//        entity.setStagedDate(OffsetDateTime.now());
//        entity.setVaultPath("vault-x");
//
//        var ocflObjectDto = new OcflObjectDto();
//        ocflObjectDto.setBagId("bagid2");
//        ocflObjectDto.setDatastation("ds1");
//        ocflObjectDto.setDataversePid("dspid");
//        ocflObjectDto.setDataversePidVersion("dspidversion");
//        ocflObjectDto.setObjectVersion("1.0");
//        ocflObjectDto.setVersionMajor(1);
//        ocflObjectDto.setVersionMinor(5);
//        ocflObjectDto.setOcflObjectPath("path/to/thing");
//        ocflObjectDto.setSwordClient("PAR");
//        ocflObjectDto.setNbn("nbn:version");
//        ocflObjectDto.setMetadata(new ObjectMapper().writeValueAsString(Map.of("data1", "5", "data2", "6")));
//        ocflObjectDto.setExportTimestamp(OffsetDateTime.now());
//
//        var part = new TarPartDto();
//        part.setPartName("0000");
//        part.setChecksumAlgorithm("md5");
//        part.setChecksumValue("thevalue");
//
//        entity.setTarParts(List.of(part));
//        entity.setOcflObjects(List.of(ocflObjectDto));
//
//        var result = new Tar();
//        result.setOcflObjectVersions(new ArrayList<>());
//        result.setTarUuid(entity.getTarUuid());
//        var ti = new OcflObjectVersion();
//        ti.setMetadata("{\"key\": 1}");
//        result.getOcflObjectVersions().add(ti);
//
//        Mockito.when(tarService.get(Mockito.any())).thenReturn(Optional.of(result));
//        Mockito.when(tarService.saveTar(Mockito.any())).thenReturn(result);
//
//        var response = EXT.target("/api/tar/" + entity.getTarUuid()).request().put(Entity.json(entity));
//        assertEquals(200, response.getStatusInfo().getStatusCode());
//
//    }
//
//
//    @Test
//    void createTarIncomplete() {
//        var entity = new Tar();
//        entity.setTarUuid("123");
//        entity.setArchivalDate(OffsetDateTime.now());
//        entity.setVaultPath("vault-x");
//
//        var response = EXT.target("/api/tar/").request().post(Entity.json(entity));
//        assertEquals(422, response.getStatusInfo().getStatusCode());
//    }
//
//    @Test
//    void createTarIncompleteMissingTarParts() {
//        var entity = new Tar();
//        entity.setTarUuid("123");
//        entity.setArchivalDate(OffsetDateTime.now());
//        entity.setStagedDate(OffsetDateTime.now());
//        entity.setVaultPath("vault-x");
//        entity.setOcflObjectVersions(
//            List.of(new OcflObjectVersion("bagid", "objectversion", null, "ds", "pid", "version", "nbn", 2, 1, "other", "version", "client", "token", "otherpath", "{}", "filepid", OffsetDateTime.now())));
//
//        var response = EXT.target("/api/tar/").request().post(Entity.json(entity));
//        assertEquals(422, response.getStatusInfo().getStatusCode());
//    }
//
//    @Test
//    void createTarIncompleteMissingOcflObjects() {
//        var entity = new Tar();
//        entity.setTarUuid("123");
//        entity.setArchivalDate(OffsetDateTime.now());
//        entity.setStagedDate(OffsetDateTime.now());
//        entity.setVaultPath("vault-x");
//        entity.setTarParts(List.of(new TarPart("0000", "md5", "value", null)));
//
//        var response = EXT.target("/api/tar/").request().post(Entity.json(entity));
//        assertEquals(422, response.getStatusInfo().getStatusCode());
//    }

    OcflObjectVersionDto createOcflVersion(Client client, CreateOcflObjectVersionRequestDto dto) throws Exception {
        var str = SUPPORT.getObjectMapper().writeValueAsString(dto);
        var bagId = UUID.randomUUID().toString();
        var version = 1;

        try (var response = client.target(
                String.format("http://localhost:%d/api/ocflObject/bagId/%s/version/%s", SUPPORT.getLocalPort(), bagId, version))
            .request()
            .put(Entity.json(str))) {

            return new OcflObjectVersionDto()
                .bagId(UUID.fromString(bagId))
                .objectVersion(version);
        }
    }
}
