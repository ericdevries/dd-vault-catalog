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

package nl.knaw.dans.catalog.core;

import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.catalog.db.Tar;
import nl.knaw.dans.catalog.db.TarDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class TarServiceImpl implements TarService {
    private static final Logger log = LoggerFactory.getLogger(TarServiceImpl.class);

    private final TarDAO tarDAO;

    public TarServiceImpl(TarDAO tarDAO) {
        this.tarDAO = tarDAO;
    }

    @Override
    public Optional<Tar> get(String id) {
        log.trace("Getting TAR with ID {}", id);
        return tarDAO.findById(id);
    }

    @Override
    public Tar saveTar(Tar tar) {
        tar.getTransferItems().forEach(t -> t.setTar(tar));
        tar.getTarParts().forEach(t -> t.setTar(tar));
        log.trace("Saving TAR {}", tar);
        return tarDAO.save(tar);
    }
}
