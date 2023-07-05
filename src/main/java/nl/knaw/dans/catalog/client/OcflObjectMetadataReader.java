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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OcflObjectMetadataReader {
    public Map<String, Object> readMetadata(String json) {
        var result = new HashMap<String, Object>();
        var config = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        var doc = JsonPath.using(config).parse(json);

        result.put("metadata_topic_classification",
            readField(doc, "$['ore:describes']['citation:Topic Classification']['topicClassification:Term']"));
        result.put("metadata_citation_email",
            readField(doc, "$['ore:describes']['citation:Contact']['datasetContact:E-mail']"));
        result.put("metadata_distributor_name",
            readField(doc, "$['ore:describes']['citation:Distributor']['distributor:Name']"));
        result.put("metadata_description",
            readField(doc, "$['ore:describes']['citation:Description']['dsDescription:Text']"));
        result.put("metadata_related_publication",
            readField(doc, "$['ore:describes']['Related Publication']['Citation']"));
        result.put("metadata_title",
            readField(doc, "$['ore:describes']['Title']"));
        result.put("metadata_subject",
            readField(doc, "$['ore:describes']['Subject']"));
        result.put("metadata_included_data_catalog",
            readField(doc, "$['ore:describes']['schema:includedInDataCatalog']"));
        result.put("metadata_dcterms_creator",
            readField(doc, "$['dcterms:creator']"));

        List<String> authorNames = doc.read("$['ore:describes']['Author'][*]['author:Name']");
        result.put("metadata_author_name", authorNames.toArray(String[]::new));

        List<String> authorAffiliations = doc.read("$['ore:describes']['Author'][*]['author:Affiliation']");
        result.put("metadata_author_affiliation", authorAffiliations.toArray(String[]::new));

        return result;
    }

    String readField(DocumentContext doc, String jsonPath) {
        var result = doc.read(jsonPath);

        if (result != null) {
            return result.toString();
        }

        return null;
    }
}
