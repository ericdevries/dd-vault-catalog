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

import nl.knaw.dans.catalog.api.Tar;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SolrServiceImpl implements SolrService {
    private final HttpSolrClient solrClient;

    public SolrServiceImpl() {
        solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr/gettingstarted").build();
    }

    @Override
    public void indexArchive(Tar tar) throws SolrServerException, IOException {
        var documents = tar.getTransferItems().stream().map(transferItem -> {
            var doc = new SolrInputDocument();
            doc.addField("datastation", transferItem.getDatastation());
            doc.addField("nbn", transferItem.getNbn());
            doc.addField("dataset_pid", transferItem.getDataversePid());
            //            doc.addField("metadata", tar.getTransferItems().stream().map(TransferItem::getMetadata).collect(Collectors.toList()));

            return doc;
        }).collect(Collectors.toList());

        solrClient.add(documents);
        solrClient.commit();
    }
}
