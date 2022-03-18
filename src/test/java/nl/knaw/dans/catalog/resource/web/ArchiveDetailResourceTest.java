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
package nl.knaw.dans.catalog.resource.web;

import com.codahale.metrics.MetricRegistry;
import freemarker.template.Configuration;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;
import nl.knaw.dans.catalog.core.OcflObjectVersionService;
import nl.knaw.dans.catalog.db.OcflObjectVersion;
import nl.knaw.dans.catalog.db.Tar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.util.List;

@ExtendWith(DropwizardExtensionsSupport.class)
class ArchiveDetailResourceTest {
    private static final OcflObjectVersionService ocflObjectVersionService = Mockito.mock(OcflObjectVersionService.class);
    private static final ResourceExtension EXT = ResourceExtension.builder()
        .addProvider(new ViewMessageBodyWriter(new MetricRegistry(), List.of(new FreemarkerViewRenderer(Configuration.VERSION_2_3_31))))
        .addResource(new ArchiveDetailResource(ocflObjectVersionService)).build();

    @AfterEach
    void tearDown() {
        Mockito.reset(ocflObjectVersionService);
    }

    @Test
    void getOK() {
        var ocflObjectVersion = new OcflObjectVersion("bagid", "objectversion", null, "ds", "pid", "version", "nbn", 2, 1, "other", "version", "client", "token", "otherpath", "{}", "filepid", OffsetDateTime.now());
        var tar = new Tar("uuid", "path", OffsetDateTime.now());
        ocflObjectVersion.setTar(tar);

        Mockito.when(ocflObjectVersionService.findByNbn(Mockito.any()))
            .thenReturn(List.of(ocflObjectVersion));

        var response = EXT.target("/nbn/urn:uuid:123")
            .request()
            .accept(MediaType.TEXT_HTML_TYPE)
            .get(Response.class);

        Assertions.assertEquals(200, response.getStatusInfo().getStatusCode());
    }

    @Test
    void getMissingNBN() {
        Mockito.when(ocflObjectVersionService.findByNbn(Mockito.any()))
            .thenReturn(List.of());

        var response = EXT.target("/nbn/urn:uuid:123")
            .request()
            .accept(MediaType.TEXT_HTML_TYPE)
            .get(Response.class);

        Assertions.assertEquals(404, response.getStatusInfo().getStatusCode());
    }
}
