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
package nl.knaw.dans.catalog.core.solr;

import lombok.Builder;
import lombok.Value;
import nl.knaw.dans.catalog.core.solr.vocabulary.DVCitation;
import nl.knaw.dans.catalog.core.solr.vocabulary.ORE;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OcflObjectMetadataReader {

    public OcflObjectMetadata readMetadata(String json) {
        var result = new HashMap<String, Set<String>>();
        var model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), null, "JSON-LD");
        model.listStatements().forEach(statement -> {
            var obj = statement.getObject();

            if (obj.isLiteral()) {
                var name = makeFieldName(statement.getPredicate());
                var value = obj.asLiteral().getValue();

                result.computeIfAbsent(name, k -> new HashSet<>()).add(value.toString());
            }
        });

        var builder = OcflObjectMetadata.builder()
            .metadata(result);

        var aggregations = model.listStatements(null, RDF.type, ORE.Aggregation);

        if (aggregations.hasNext()) {
            var resource = aggregations.next().getSubject();

            builder.title(getRDFProperty(resource, DCTerms.title));
            builder.description(getEmbeddedRDFProperty(resource, DVCitation.dsDescription, DVCitation.dsDescriptionValue));
        }

        return builder.build();
    }

    private String getEmbeddedRDFProperty(Resource resource, Property parent, Property child) {
        var results = new HashSet<String>();

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

        // ensure we get deterministic results
        var list = new ArrayList<>(results);
        list.sort(String::compareTo);

        return StringUtils.join(list, "; ");
    }

    private String getRDFProperty(Resource resource, Property name) {
        var results = new HashSet<String>();

        resource.listProperties(name).forEachRemaining(item -> {
            results.add(item.getObject().asLiteral().getString());
        });

        if (results.size() == 0) {
            return null;
        }

        // ensure we get deterministic results
        var list = new ArrayList<>(results);
        list.sort(String::compareTo);

        return StringUtils.join(list, "; ");
    }

    String makeFieldName(Property prop) {
        var uri = URI.create(prop.getURI());
        var stripped = uri.getSchemeSpecificPart();

        if (uri.getFragment() != null) {
            stripped += "#" + uri.getFragment();
        }

        return stripped.toLowerCase()
            .replaceFirst("^//", "")
            .replaceAll("[^a-zA-Z0-9]", "_");
    }

    @Value
    @Builder
    public static class OcflObjectMetadata {
        Map<String, Set<String>> metadata;
        String title;
        String description;
    }

}
