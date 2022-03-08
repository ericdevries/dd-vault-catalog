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
import nl.knaw.dans.catalog.api.TarOld;
import nl.knaw.dans.catalog.db.Tar;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SolrServiceImpl implements SolrService {
    private static final Logger log = LoggerFactory.getLogger(SolrServiceImpl.class);

    private final HttpSolrClient solrClient;

    public SolrServiceImpl(DdVaultCatalogConfiguration.SolrConfig solrConfig) {
        solrClient = new HttpSolrClient.Builder(solrConfig.getUrl()).build();
    }

    @Override
    public void indexArchive(Tar tar) throws SolrServerException, IOException {
        Objects.requireNonNull(tar, "tar cannot be null");

        log.trace("Indexing archive {}", tar);
        var documents = tar.getTransferItems().stream().map(transferItem -> {
            var doc = new SolrInputDocument();
            doc.addField("datastation", transferItem.getDatastation());
            doc.addField("nbn", transferItem.getNbn());
            doc.addField("dataset_pid", transferItem.getDataversePid());

            flattenMetadata(transferItem.getMetadata())
                .forEach(doc::addField);

            log.trace("Document generated: {}", doc);
            return doc;
        }).collect(Collectors.toList());

        log.debug("Indexing document with ID {}", tar.getTarUuid());
        solrClient.add(documents);
        solrClient.commit();
    }

    Map<String, String> flattenMetadata(String str) {
        // TODO read metadata from json
        //            doc.addField("metadata_dcterms_creator", transferItem.getMetadata().get("dcterms:creator"));
        return Map.of();
    }
}
