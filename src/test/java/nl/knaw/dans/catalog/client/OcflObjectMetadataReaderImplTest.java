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
package nl.knaw.dans.catalog.client;

import nl.knaw.dans.catalog.core.solr.OcflObjectMetadataReader;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class OcflObjectMetadataReaderImplTest {

    @Test
    void readMetadata_should_populate_output() throws Exception {
        var reader = new OcflObjectMetadataReader();
        var json = readJson("vault-ingest-flow.jsonld");
        var result = reader.readMetadata(json);

        assertThat(result.getMetadata().get("dataverse_org_schema_citation_contributortype"))
            .containsOnly("Sponsor", "ProjectMember");

        assertThat(result.getTitle())
            .isEqualTo("A bag containing examples for each mapping rule");

        assertThat(result.getDescription())
            .contains("Even more descriptions");
    }

    // TODO this is a nice little test class to populate solr
    // but it should not be a real test
    // think about how we can create some utility to call this code
    //    @Test
    //    void readMetadata_should_parse_vaultCatalogJson_file() throws Exception {
    //        var reader = new OcflObjectMetadataReader();
    //        var config = new DdVaultCatalogConfiguration.SolrConfig();
    //        config.setUrl("http://localhost:8983/solr/");
    //        config.setSchema("vault-catalog");
    //        var service = new SolrServiceImpl(config, reader);
    //
    //        var ocflObjectVersion = OcflObjectVersion.builder()
    //            .objectVersion(1)
    //            .bagId("ae313a5d-c753-473a-a555-60d280365ea8")
    //            .nbn("urn:nbn:nl:ui:13-4n3-3")
    //            .dataSupplier("DANS")
    //            .ocflObjectPath("ae/31/ad/bd/rest-of-folder")
    //            .build();
    //
    //        {
    //            var path = Objects.requireNonNull(getClass().getResource("/json/dataverse.jsonld")).getPath();
    //            var json = Files.readString(Path.of(path));
    //            ocflObjectVersion.setMetadata(json);
    //
    //            service.indexOcflObjectVersion(ocflObjectVersion);
    //        }
    //        {
    //            var path = Objects.requireNonNull(getClass().getResource("/json/vault-ingest-flow.jsonld")).getPath();
    //            var json = Files.readString(Path.of(path));
    //            ocflObjectVersion.setBagId("16e8f88f-d658-494e-8ba7-bcda3eff2102");
    //            ocflObjectVersion.setMetadata(json);
    //            ocflObjectVersion.setObjectVersion(2);
    //
    //            service.indexOcflObjectVersion(ocflObjectVersion);
    //        }
    //    }

    private String readJson(String name) throws Exception {
        var path = Objects.requireNonNull(getClass().getResource("/json/" + name)).getPath();
        return Files.readString(Path.of(path));
    }
}


