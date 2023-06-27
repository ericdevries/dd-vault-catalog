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
import nl.knaw.dans.catalog.core.OcflObjectVersionRepository;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersionId;
import nl.knaw.dans.catalog.core.exception.OcflObjectVersionNotFoundException;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class OcflObjectVersionEntityRepository extends AbstractDAO<OcflObjectVersionEntity> implements OcflObjectVersionRepository {
    public OcflObjectVersionEntityRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Optional<OcflObjectVersionEntity> findByBagIdAndVersion(String bagId, int version) {
        return query("from OcflObjectVersionEntity where bagId = :bagId and objectVersion = :version order by id")
            .setParameter("bagId", bagId)
            .setParameter("version", version)
            .uniqueResultOptional();
    }

    @Override
    public Optional<OcflObjectVersionEntity> findLatestByBagId(String bagId) {
        return query("from OcflObjectVersionEntity where bagId = :bagId order by objectVersion desc limit 1")
            .setParameter("bagId", bagId)
            .uniqueResultOptional();
    }

    @Override
    public List<OcflObjectVersionEntity> findAllByBagId(String bagId) {
        return new ArrayList<>(
            query("from OcflObjectVersionEntity where bagId = :bagId order by objectVersion desc limit 1")
                .setParameter("bagId", bagId)
                .list()
        );
    }

    @Override
    public List<OcflObjectVersionEntity> findAll() {
        return new ArrayList<>(list(query("from OcflObjectVersionEntity")));
    }

    @Override
    public List<OcflObjectVersionEntity> findAllBySwordToken(String swordToken) {
        return new ArrayList<>(
            query("from OcflObjectVersionEntity where swordToken = :swordToken order by objectVersion desc limit 1")
                .setParameter("swordToken", swordToken)
                .list()
        );
    }

    @Override
    public OcflObjectVersionEntity save(OcflObjectVersionEntity ocflObjectVersion) {
        return persist(ocflObjectVersion);
    }

    @Override
    public List<OcflObjectVersionEntity> findAll(Collection<OcflObjectVersionId> versions) throws OcflObjectVersionNotFoundException {
        var ocflObjectVersions = new ArrayList<OcflObjectVersionEntity>();

        if (versions != null) {
            for (var version : versions) {
                var ocflObjectVersion = findByBagIdAndVersion(version.getBagId(), version.getObjectVersion())
                    .orElseThrow(() -> new OcflObjectVersionNotFoundException(
                        String.format("OcflObjectVersion with bagId %s and version %d not found", version.getBagId(), version.getObjectVersion())
                    ));

                ocflObjectVersions.add(ocflObjectVersion);
            }
        }

        return ocflObjectVersions;
    }

    @Override
    public List<OcflObjectVersionEntity> findByNbn(String nbn) {
        return new ArrayList<>(
            list(
                query(
                    "from OcflObjectVersionEntity where nbn = :nbn order by objectVersion desc"
                ).setParameter("nbn", nbn)
            )
        );
    }
}
