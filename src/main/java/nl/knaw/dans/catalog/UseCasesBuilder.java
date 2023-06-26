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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import nl.knaw.dans.catalog.core.*;
import nl.knaw.dans.catalog.db.OcflObjectVersionEntityFactory;
import nl.knaw.dans.catalog.db.OcflObjectVersionEntityRepository;
import nl.knaw.dans.catalog.db.TarEntityFactory;
import nl.knaw.dans.catalog.db.TarEntityRepository;

public class UseCasesBuilder {

    public static UseCases build(DdVaultCatalogConfiguration configuration, ObjectMapper objectMapper, HibernateBundle<DdVaultCatalogConfiguration> hibernateBundle) {
        var ocflObjectMetadataReader = new OcflObjectMetadataReader();
        var searchIndex = new SolrServiceImpl(configuration.getSolr(), ocflObjectMetadataReader);

        var oclfObjectFactory = new OcflObjectVersionEntityFactory(objectMapper);
        var ocflObjectVersionRepository = new OcflObjectVersionEntityRepository(hibernateBundle.getSessionFactory());

        var tarRepository = new TarEntityRepository(hibernateBundle.getSessionFactory());
        var tarFactory = new TarEntityFactory();

        return new UnitOfWorkAwareProxyFactory(hibernateBundle)
            .create(UseCases.class,
                new Class[]{
                    OcflObjectVersionRepository.class,
                    OcflObjectVersionFactory.class,
                    TarRepository.class,
                    TarFactory.class,
                    SearchIndex.class,
                },
                new Object[]{
                    ocflObjectVersionRepository,
                    oclfObjectFactory,
                    tarRepository,
                    tarFactory,
                    searchIndex
                }
            );
    }
}
