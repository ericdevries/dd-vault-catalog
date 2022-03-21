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

import nl.knaw.dans.catalog.DdVaultCatalogConfiguration;
import nl.knaw.dans.catalog.db.Tar;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolrServiceImpl implements SolrService {
    private static final Logger log = LoggerFactory.getLogger(SolrServiceImpl.class);

    private final HttpSolrClient solrClient;

    public SolrServiceImpl(DdVaultCatalogConfiguration.SolrConfig solrConfig) {
        solrClient = new HttpSolrClient.Builder(solrConfig.getUrl()).build();
    }

    /**
     * ## ids
     * bag_id: string
     * tar_id: string
     * nbn: string
     * dataverse_pid: string
     * sword_client: string
     * sword_token: string
     * ocfl_object_path: string
     * tar_part_name: string
     *
     *
     * ## timestamps
     * tar_archival_date
     * export_timestamp
     *
     * ## text fields
     * filepid_to_local_path
     *
     * ## int fields
     * version_major
     * version_minor
     *
     *
     * ## metadata fields
     * metadata_citation_topic_classification
     * metadata_author_name
     * metadata_author_affiliation
     * metadata_citation_email
     * metadata_citation_distributor
     * metadata_citation_description
     * metadata_related_publication
     * metadata_title
     * metadata_subject
     * metadata_data_catalog
     *
     */
    @Override
    public void indexArchive(Tar tar) throws SolrServerException, IOException {
        /*
        Objects.requireNonNull(tar, "tar cannot be null");

        log.trace("Indexing archive {}", tar);
        var documents = tar.getOcflObjectVersions().stream().map(ocflObjectVersion -> {
            var doc = new SolrInputDocument();
            doc.addField("datastation", ocflObjectVersion.getDatastation());
            doc.addField("nbn", ocflObjectVersion.getNbn());
            doc.addField("dataset_pid", ocflObjectVersion.getDataversePid());

            flattenMetadata(ocflObjectVersion.getMetadata())
                .forEach(doc::addField);

            log.trace("Document generated: {}", doc);
            return doc;
        }).collect(Collectors.toList());

        log.debug("Indexing document with ID {}", tar.getTarUuid());
        solrClient.add(documents);
        solrClient.commit();

         */
    }

    @Override
    public List<Tar> searchArchives(String query) {
        return null;
    }

    Map<String, String> flattenMetadata(String str) {
        // TODO read metadata from json
        //            doc.addField("metadata_dcterms_creator", transferItem.getMetadata().get("dcterms:creator"));
        return Map.of();
    }
}
