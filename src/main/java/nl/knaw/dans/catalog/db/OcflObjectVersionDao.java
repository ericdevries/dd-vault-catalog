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
package nl.knaw.dans.catalog.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class OcflObjectVersionDao extends AbstractDAO<OcflObjectVersion> {

    public OcflObjectVersionDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<OcflObjectVersion> findByNbn(String nbn) {
        var query = currentSession().createQuery(
            "from OcflObjectVersion where nbn = :nbn "
                + "order by id.versionMajor desc, id.versionMinor desc", OcflObjectVersion.class);

        query.setParameter("nbn", nbn);

        return query.list();
    }

    public Optional<OcflObjectVersion> findByBagIdAndVersion(String bagId, int versionMajor, int versionMinor) {
        var query = currentSession().createQuery(
            "from OcflObjectVersion where id.bagId = :bagId "
                + "and id.versionMajor = :versionMajor and id.versionMinor = :versionMinor", OcflObjectVersion.class);

        query.setParameter("bagId", bagId);
        query.setParameter("versionMajor", versionMajor);
        query.setParameter("versionMinor", versionMinor);

        return query.uniqueResultOptional();
    }

    public List<OcflObjectVersion> findAll() {
        return currentSession().createQuery("from OcflObjectVersion", OcflObjectVersion.class)
            .list();
    }
}
