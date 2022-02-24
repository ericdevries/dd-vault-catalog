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

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.jersey.errors.ErrorEntityWriter;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.View;
import io.dropwizard.views.ViewBundle;
import nl.knaw.dans.catalog.core.SolrServiceImpl;
import nl.knaw.dans.catalog.core.TarServiceImpl;
import nl.knaw.dans.catalog.core.TransferItemServiceImpl;
import nl.knaw.dans.catalog.db.TarModel;
import nl.knaw.dans.catalog.db.TarModelDAO;
import nl.knaw.dans.catalog.db.TarPartModel;
import nl.knaw.dans.catalog.db.TransferItemDao;
import nl.knaw.dans.catalog.db.TransferItemModel;
import nl.knaw.dans.catalog.resource.ArchiveDetailResource;
import nl.knaw.dans.catalog.resource.TarAPIResource;
import nl.knaw.dans.catalog.resource.view.ErrorView;

import javax.ws.rs.core.MediaType;

public class DdVaultCatalogApplication extends Application<DdVaultCatalogConfiguration> {
    private final HibernateBundle<DdVaultCatalogConfiguration> hibernateBundle = new HibernateBundle<>(TransferItemModel.class, TarModel.class, TarPartModel.class) {

        @Override
        public PooledDataSourceFactory getDataSourceFactory(DdVaultCatalogConfiguration configuration) {
            return configuration.getDatabase();
        }
    };

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
    }

    @Override
    public void run(final DdVaultCatalogConfiguration configuration, final Environment environment) {

        var tarModelDao = new TarModelDAO(hibernateBundle.getSessionFactory());
        var transferItemDao = new TransferItemDao(hibernateBundle.getSessionFactory());
        var tarService = new UnitOfWorkAwareProxyFactory(hibernateBundle).create(TarServiceImpl.class,
            TarModelDAO.class, tarModelDao);

        var transferItemService = new UnitOfWorkAwareProxyFactory(hibernateBundle).create(TransferItemServiceImpl.class,
            TransferItemDao.class, transferItemDao);

        var solrService = new SolrServiceImpl(configuration.getSolr());

        environment.jersey().register(new TarAPIResource(tarService, solrService));
        environment.jersey().register(new ArchiveDetailResource(transferItemService));
        environment.jersey().register(new ErrorEntityWriter<ErrorMessage, View>(MediaType.TEXT_HTML_TYPE, View.class) {

            @Override
            protected View getRepresentation(ErrorMessage errorMessage) {
                return new ErrorView(errorMessage);
            }
        });

    }
}
