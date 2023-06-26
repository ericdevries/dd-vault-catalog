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
package nl.knaw.dans.catalog.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OcflObjectMetadataReaderImplTest {

    @Test
    void readMetadata() {
        var reader = new OcflObjectMetadataReader();
        var json = "{\"dcterms:modified\":\"2021-11-17\",\"dcterms:creator\":\"DANS Archaeology Data Station (dev)\",\"@type\":\"ore:ResourceMap\",\"@id\":\"https://dar.dans.knaw.nl/api/datasets/export?exporter=OAI_ORE&persistentId=doi:10.5072/DAR/KXTEQT\",\"ore:describes\":{\"citation:Topic Classification\":{\"topicClassification:Term\":\"Public health\"}}}";
        var result = reader.readMetadata(json);

        assertEquals("Public health", result.get("metadata_topic_classification"));
        assertEquals("DANS Archaeology Data Station (dev)", result.get("metadata_dcterms_creator"));
    }

    @Test
    void readFullDocument() {
        var reader = new OcflObjectMetadataReader();
        var json = "{\"@context\": {\"Author\": \"http://purl.org/dc/terms/creator\", \"Citation\": \"http://purl.org/dc/terms/bibliographicCitation\", \"Deposit Date\": \"http://purl.org/dc/terms/dateSubmitted\", \"Related Publication\": \"http://purl.org/dc/terms/isReferencedBy\", \"Subject\": \"http://purl.org/dc/terms/subject\", \"Title\": \"http://purl.org/dc/terms/title\", \"author\": \"https://dataverse.org/schema/citation/author#\", \"citation\": \"https://dataverse.org/schema/citation/\", \"dansDataVaultMetadata\": \"https://dar.dans.knaw.nl/schema/dansDataVaultMetadata#\", \"datasetContact\": \"https://dataverse.org/schema/citation/datasetContact#\", \"dcterms\": \"http://purl.org/dc/terms/\", \"distributor\": \"https://dataverse.org/schema/citation/distributor#\", \"dsDescription\": \"https://dataverse.org/schema/citation/dsDescription#\", \"dvcore\": \"https://dataverse.org/schema/core#\", \"ore\": \"http://www.openarchives.org/ore/terms/\", \"schema\": \"http://schema.org/\", \"topicClassification\": \"https://dataverse.org/schema/citation/topicClassification#\"}, \"@id\": \"https://dar.dans.knaw.nl/api/datasets/export?exporter=OAI_ORE&persistentId=doi:10.5072/DAR/KXTEQT\", \"@type\": \"ore:ResourceMap\", \"dcterms:creator\": \"DANS Archaeology Data Station (dev)\", \"dcterms:modified\": \"2021-11-17\", \"ore:describes\": {\"@id\": \"doi:10.5072/DAR/KXTEQT\", \"@type\": [\"ore:Aggregation\", \"schema:Dataset\"], \"Author\": [{\"author:Affiliation\": \"University of Genova\", \"author:Name\": \"Girosi, Frederico\"}, {\"author:Affiliation\": \"Harvard University\", \"author:Name\": \"King, Gary\"} ], \"Deposit Date\": \"2006\", \"Related Publication\": {\"Citation\": \"Girosi, Federico, and Gary King. 2008\"}, \"Subject\": \"Social Sciences\", \"Title\": \"Cause of Death Data\", \"citation:Contact\": {\"datasetContact:E-mail\": \"king@mailinator.com\"}, \"citation:Description\": {\"dsDescription:Text\": \"Description text...\"}, \"citation:Distribution Date\": \"2008\", \"citation:Distributor\": {\"distributor:Logo URL\": \"https://dataverse.harvard.edu/resources/images/dataverseproject_logo.jpg\", \"distributor:Name\": \"Harvard Dataverse\"}, \"citation:Production Date\": \"2006\", \"citation:Topic Classification\": {\"topicClassification:Term\": \"Public health\"}, \"dansDataVaultMetadata:Bag ID\": \"urn:uuid:b0b80ccd-504a-4167-8d80-90ee6c478b46\", \"dansDataVaultMetadata:DV PID\": \"doi:10.5072/DAR/KXTEQT\", \"dansDataVaultMetadata:DV PID Version\": \"1.0\", \"dansDataVaultMetadata:NBN\": \"urn:nbn:nl:ui:13-50853d9d-ff88-4546-9bce-e73c3698a2c1\", \"dvcore:fileTermsOfAccess\": {\"dvcore:fileRequestAccess\": false }, \"ore:aggregates\": [{\"@id\": \"https://dar.dans.knaw.nl/file.xhtml?fileId=76\", \"@type\": \"ore:AggregatedResource\", \"dvcore:UNF\": \"UNF:6:3bpD7h1cFrQ8vxRfPkB5FQ==\", \"dvcore:checksum\": {\"@type\": \"SHA-1\", \"@value\": \"11132116be9dbd3990fad80cb075892b31fae67f\"}, \"dvcore:datasetVersionId\": 12, \"dvcore:filesize\": 223116, \"dvcore:originalFileFormat\": \"application/x-spss-sav\", \"dvcore:originalFormatLabel\": \"SPSS Binary\", \"dvcore:restricted\": false, \"dvcore:rootDataFileId\": -1, \"dvcore:storageIdentifier\": \"file://17d2d94e3ea-3c9ca25b9216\", \"schema:description\": \"\", \"schema:fileFormat\": \"text/tab-separated-values\", \"schema:name\": \"adjacency_subset.tab\", \"schema:sameAs\": \"https://dar.dans.knaw.nl/api/access/datafile/76\", \"schema:version\": 3 }, {\"@id\": \"https://dar.dans.knaw.nl/file.xhtml?fileId=77\", \"@type\": \"ore:AggregatedResource\", \"dvcore:UNF\": \"UNF:6:vnYBZZYwOdXAbPDCWDdFog==\", \"dvcore:checksum\": {\"@type\": \"SHA-1\", \"@value\": \"3f80b2e881ea0cbdecb820cee00b3bc7506c3916\"}, \"dvcore:datasetVersionId\": 12, \"dvcore:filesize\": 4873494, \"dvcore:originalFileFormat\": \"application/x-spss-sav\", \"dvcore:originalFormatLabel\": \"SPSS Binary\", \"dvcore:restricted\": false, \"dvcore:rootDataFileId\": -1, \"dvcore:storageIdentifier\": \"file://17d2d94ef1f-2c1f41c40ea2\", \"schema:description\": \"\", \"schema:fileFormat\": \"text/tab-separated-values\", \"schema:name\": \"allc_subset.tab\", \"schema:sameAs\": \"https://dar.dans.knaw.nl/api/access/datafile/77\", \"schema:version\": 3 } ], \"schema:dateModified\": \"2021-11-17 12:08:07.114\", \"schema:datePublished\": \"2021-11-17\", \"schema:hasPart\": [\"https://dar.dans.knaw.nl/file.xhtml?fileId=76\", \"https://dar.dans.knaw.nl/file.xhtml?fileId=77\"], \"schema:includedInDataCatalog\": \"DANS Archaeology Data Station (dev) 2\", \"schema:name\": \"Cause of Death Data\", \"schema:version\": \"1.0\"} }";
        var result = reader.readMetadata(json);

        assertEquals("Public health", result.get("metadata_topic_classification"));
        assertArrayEquals(new String[] { "Girosi, Frederico", "King, Gary" }, (Object[]) result.get("metadata_author_name"));
        assertArrayEquals(new String[] { "University of Genova", "Harvard University" }, (Object[]) result.get("metadata_author_affiliation"));
        assertEquals("king@mailinator.com", result.get("metadata_citation_email"));
        assertEquals("Harvard Dataverse", result.get("metadata_distributor_name"));
        assertEquals("Description text...", result.get("metadata_description"));
        assertEquals("Girosi, Federico, and Gary King. 2008", result.get("metadata_related_publication"));
        assertEquals("Cause of Death Data", result.get("metadata_title"));
        assertEquals("Social Sciences", result.get("metadata_subject"));
        assertEquals("DANS Archaeology Data Station (dev) 2", result.get("metadata_included_data_catalog"));
        assertEquals("DANS Archaeology Data Station (dev)", result.get("metadata_dcterms_creator"));
    }

    @Test
    void readEmptyDocument() {
        var reader = new OcflObjectMetadataReader();
        var json = "{}";
        var result = reader.readMetadata(json);

        assertNull(result.get("metadata_topic_classification"));
        assertArrayEquals(new String[] {}, (Object[]) result.get("metadata_author_name"));
        assertArrayEquals(new String[] {}, (Object[]) result.get("metadata_author_affiliation"));
        assertNull(result.get("metadata_citation_email"));
        assertNull(result.get("metadata_distributor_name"));
        assertNull(result.get("metadata_description"));
        assertNull(result.get("metadata_related_publication"));
        assertNull(result.get("metadata_title"));
        assertNull(result.get("metadata_subject"));
        assertNull(result.get("metadata_included_data_catalog"));
        assertNull(result.get("metadata_dcterms_creator"));
    }
}

