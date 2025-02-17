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

import nl.knaw.dans.catalog.core.domain.OcflObjectVersionId;
import nl.knaw.dans.catalog.core.exception.OcflObjectVersionNotFoundException;
import nl.knaw.dans.catalog.db.OcflObjectVersion;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OcflObjectVersionRepository {

    Optional<OcflObjectVersion> findByBagIdAndVersion(String bagId, int version);

    List<OcflObjectVersion> findAllByBagId(String bagId);

    List<OcflObjectVersion> findAll();

    List<OcflObjectVersion> findAllBySwordToken(String swordToken);

    OcflObjectVersion save(OcflObjectVersion ocflObjectVersion);

    List<OcflObjectVersion> findAll(Collection<OcflObjectVersionId> versions) throws OcflObjectVersionNotFoundException;

    List<OcflObjectVersion> findByNbn(String nbn);
}
