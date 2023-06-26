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

import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
class TarDAOTest {
    private static final Logger log = LoggerFactory.getLogger(TarDAOTest.class);

    private final DAOTestExtension daoTestRule = DAOTestExtension.newBuilder()
        .addEntityClass(Tar.class)
        .addEntityClass(TarPart.class)
        .addEntityClass(OcflObjectVersion.class)
        .build();

    private TarDAO tarDAO;
    private TarPartDao tarPartDao;
    private OcflObjectVersionDao ocflObjectVersionDao;

    @BeforeEach
    void setUp() {
        tarDAO = new TarDAO(daoTestRule.getSessionFactory());
        tarPartDao = new TarPartDao(daoTestRule.getSessionFactory());
        ocflObjectVersionDao = new OcflObjectVersionDao(daoTestRule.getSessionFactory());
    }

    @Test
    void insertTar() {
        daoTestRule.inTransaction(() -> {
            var tar = new Tar("uuid1", "path1", OffsetDateTime.now());
            var ocflObjectVersion = new OcflObjectVersion("bagid", "objectversion", tar, "ds", "pid", "version", "nbn", 2, 1, "other", "version", "client", "token", "otherpath", "{}", "filepid",
                OffsetDateTime.now());

            tar.setOcflObjectVersions(List.of(ocflObjectVersion));
            tar.setTarParts(List.of(new TarPart("0000", "md5", "cs", tar)));

            tarDAO.save(tar);
            tarDAO.evict(tar);
        });

        var items = tarDAO.findAll();

        assertThat(items).extracting("tarUuid").containsOnly("uuid1");
    }

    @Test
    void updateTar() {
        daoTestRule.inTransaction(() -> {
            var tar = new Tar("uuid1", "path1", OffsetDateTime.now());
            var ocflObjectVersion = new OcflObjectVersion("bagid", "objectversion", tar, "ds", "pid", "version", "nbn", 2, 1, "other", "version", "client", "token", "otherpath", "{}", "filepid",
                OffsetDateTime.now());

            tar.setOcflObjectVersions(List.of(ocflObjectVersion));
            tar.setTarParts(List.of(new TarPart("0000", "md5", "cs", tar)));

            tarDAO.save(tar);
            tarDAO.evict(tar);
        });

        daoTestRule.inTransaction(() -> {
            var existing = tarDAO.findById("uuid1");
            Assertions.assertTrue(existing.isPresent());
            Assertions.assertEquals("uuid1", existing.get().getTarUuid());

            var newTar = new Tar("uuid1", "path2", OffsetDateTime.now());
            var newOcflObjectVersion = new OcflObjectVersion("bagid", "objectversion 2", existing.get(), "ds", "pid", "version", "nbn", 2, 1, "other", "version", "client", "token", "otherpath", "{}",
                "filepid",
                OffsetDateTime.now());
            newTar.setTarParts(List.of(new TarPart("0000", "md5", "cs2", existing.get())));
            newTar.setOcflObjectVersions(List.of(newOcflObjectVersion));

            tarDAO.save(tarDAO.merge(newTar));
        });

        var tars = tarDAO.findAll();

        Assertions.assertEquals(1, tars.size());

        var thisTar = tars.get(0);
        Assertions.assertEquals(1, thisTar.getTarParts().size());

        Assertions.assertEquals("path2", thisTar.getVaultPath());

        var ocflObjectVersions = ocflObjectVersionDao.findAll();
        assertThat(ocflObjectVersions)
            .extracting("objectVersion")
            .containsOnly("objectversion 2");

        var tarParts = tarPartDao.findAll();
        assertThat(tarParts)
            .extracting("checksumValue")
            .containsOnly("cs2");
    }
}
