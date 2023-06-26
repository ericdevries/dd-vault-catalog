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
import nl.knaw.dans.catalog.db.TarEntity;
import nl.knaw.dans.catalog.db.TarPartEntity;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class SolrServiceImpl implements SearchIndex {
    private static final Logger log = LoggerFactory.getLogger(SolrServiceImpl.class);

    private final HttpSolrClient solrClient;
    private final OcflObjectMetadataReader ocflObjectMetadataReader;

    public SolrServiceImpl(DdVaultCatalogConfiguration.SolrConfig solrConfig, OcflObjectMetadataReader ocflObjectMetadataReader) {
        if (solrConfig != null) {
            solrClient = new HttpSolrClient.Builder(solrConfig.getUrl()).build();
            this.ocflObjectMetadataReader = ocflObjectMetadataReader;
        }
        else {
            solrClient = null;
            this.ocflObjectMetadataReader = null;
        }
    }

    /**
     * ## ids bag_id: string tar_id: string nbn: string dataverse_pid: string sword_client: string sword_token: string ocfl_object_path: string tar_part_name: string
     * <p>
     * <p>
     * ## timestamps tar_archival_date export_timestamp
     * <p>
     * ## text fields filepid_to_local_path
     * <p>
     * ## int fields version_major version_minor
     */
    @Override
    public void indexTar(TarEntity tar) {
        if (solrClient == null) {
            log.warn("Solr is not configured, skipping indexing of tar {}", tar.getTarUuid());
            return;
        }

        try {
            var documents = tar.getOcflObjectVersions().stream().map(ocflObjectVersion -> {
                var doc = new SolrInputDocument();
                var id = String.format("%s/%s", ocflObjectVersion.getId().getBagId(), ocflObjectVersion.getId().getObjectVersion());

                doc.setField("id", id);
                doc.setField("bag_id", ocflObjectVersion.getId().getBagId());
                doc.setField("tar_id", ocflObjectVersion.getTar().getTarUuid());
                doc.setField("nbn", ocflObjectVersion.getNbn());
                doc.setField("dataverse_pid", ocflObjectVersion.getDataversePid());
                doc.setField("datastation", ocflObjectVersion.getDataSupplier());

//            doc.setField("sword_client", ocflObjectVersion.getsw());
                doc.setField("sword_token", ocflObjectVersion.getSwordToken());
                doc.setField("ocfl_object_path", ocflObjectVersion.getOcflObjectPath());
                doc.setField("tar_part_name", ocflObjectVersion.getTar().getTarParts().stream().map(TarPartEntity::getPartName).collect(Collectors.toList()));

                doc.setField("tar_archival_timestamp", formatDate(ocflObjectVersion.getTar().getArchivalDate()));
                doc.setField("export_timestamp", formatDate(ocflObjectVersion.getExportTimestamp()));

                var metadata = ocflObjectMetadataReader.readMetadata(ocflObjectVersion.getMetadataString());

                for (var entry : metadata.entrySet()) {
                    doc.addField(entry.getKey(), entry.getValue());
                }

                doc.addField("_text_", ocflObjectVersion.getId().getBagId().replace("urn:uuid:", ""));
                doc.addField("_text_", ocflObjectVersion.getNbn().replace("urn:nbn:nl:ui:", ""));

                log.trace("Document generated: {}", doc);
                return doc;
            }).collect(Collectors.toList());

            log.debug("Indexing document with ID {}", tar.getTarUuid());
            solrClient.add(documents);
            solrClient.commit();
        }
        catch (SolrServerException | IOException e) {
            log.error("Error indexing tar {}", tar.getTarUuid(), e);
        }
    }

    String formatDate(OffsetDateTime date) {
        if (date == null) {
            return null;
        }

        return date.format(DateTimeFormatter.ISO_DATE_TIME);
    }

}
