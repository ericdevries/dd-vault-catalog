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

package nl.knaw.dans.catalog;

import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.Application;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jersey.errors.ErrorEntityWriter;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.View;
import io.dropwizard.views.ViewBundle;
import nl.knaw.dans.catalog.cli.ReindexCommand;
import nl.knaw.dans.catalog.resource.api.OcflObjectApiResource;
import nl.knaw.dans.catalog.resource.api.TarAPIResource;
import nl.knaw.dans.catalog.resource.view.ErrorView;
import nl.knaw.dans.catalog.resource.web.ArchiveDetailResource;

import javax.ws.rs.core.MediaType;

public class DdVaultCatalogApplication extends Application<DdVaultCatalogConfiguration> {
    private final HibernateBundle<DdVaultCatalogConfiguration> hibernateBundle = new DdVaultHibernateBundle();

    public static void main(final String[] args) throws Exception {
        new DdVaultCatalogApplication().run(args);
    }

    @Override
    public String getName() {
        return "Dd Vault Catalog";
    }

    @Override
    public void initialize(final Bootstrap<DdVaultCatalogConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        bootstrap.addCommand(new ReindexCommand(hibernateBundle));
    }

    @Override
    public void run(final DdVaultCatalogConfiguration configuration, final Environment environment) {
        var useCases = UseCasesBuilder.build(configuration, environment.getObjectMapper(), hibernateBundle);

        environment.jersey().register(new TarAPIResource(useCases));
        environment.jersey().register(new OcflObjectApiResource(useCases));
        environment.jersey().register(new ArchiveDetailResource(useCases));
        environment.jersey().register(new ErrorEntityWriter<ErrorMessage, View>(MediaType.TEXT_HTML_TYPE, View.class) {

            @Override
            protected View getRepresentation(ErrorMessage errorMessage) {
                return new ErrorView(errorMessage);
            }
        });
    }
}
