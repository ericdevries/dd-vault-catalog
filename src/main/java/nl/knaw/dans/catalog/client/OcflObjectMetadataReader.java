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

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OcflObjectMetadataReader {

    public OcflObjectMetadata readMetadata(String json) {
        var result = new HashMap<String, Object>();
        var model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(json.getBytes()), null, "JSON-LD");
        model.listStatements().forEach(statement -> {
            //            System.out.println(statement);
            var obj = statement.getObject();

            if (obj.isLiteral()) {
                var name = makeFieldName(statement.getPredicate());
                var value = obj.asLiteral().getValue();
                result.put(name, value.toString());
            }
        });

        var builder = OcflObjectMetadata.builder()
            .metadata(result);

        var aggregationNS = model.createProperty("http://www.openarchives.org/ore/terms/", "Aggregation");
        var aggregations = model.listStatements(null, RDF.type, aggregationNS);

        if (aggregations.hasNext()) {
            var resource = aggregations.next().getSubject();

            builder.title(getRDFProperty(resource, DCTerms.title));
            builder.description(getEmbeddedRDFProperty(resource, DVCitation.dsDescription, DVCitation.dsDescriptionValue));
        }

        return builder.build();
    }

    private String getEmbeddedRDFProperty(Resource resource, Property parent, Property child) {
        var results = new ArrayList<String>();
        resource.listProperties(parent)
            .forEachRemaining(item -> {
                var value = getRDFProperty(item.getObject().asResource(), child);

                if (value != null) {
                    results.add(value);
                }
            });

        if (results.size() == 0) {
            return null;
        }

        return StringUtils.join(results, "; ");
    }

    private String getRDFProperty(Resource resource, Property name) {
        var results = new ArrayList<String>();

        resource.listProperties(name).forEachRemaining(item -> {
            results.add(item.getObject().asLiteral().getString());
        });

        if (results.size() == 0) {
            return null;
        }

        return StringUtils.join(results, "; ");
    }

    String makeFieldName(Property prop) {
        return URI.create(prop.getURI())
            .getSchemeSpecificPart()
            .toLowerCase()
            .replaceFirst("^//", "")
            .replaceAll("[^a-zA-Z0-9]", "_");
    }

    @Value
    @Builder
    public static class OcflObjectMetadata {
        Map<String, Object> metadata;
        String title;
        String description;
    }

}
