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

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.dans.catalog.core.OcflObjectVersionFactory;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersion;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersionId;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersionParameters;
import nl.knaw.dans.catalog.core.exception.OcflObjectVersionAlreadyExistsException;
import nl.knaw.dans.catalog.core.exception.OcflObjectVersionIllegalArgumentException;

public class OcflObjectVersionEntityFactory implements OcflObjectVersionFactory {
    private final ObjectMapper objectMapper;

    public OcflObjectVersionEntityFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public OcflObjectVersion create(OcflObjectVersionId id, OcflObjectVersionParameters parameters) throws OcflObjectVersionAlreadyExistsException, OcflObjectVersionIllegalArgumentException {
        var result =  OcflObjectVersionEntity.builder()
            .objectMapper(objectMapper)
            .bagId(id.getBagId())
            .objectVersion(id.getObjectVersion())
            .swordToken(parameters.getSwordToken())
            .nbn(parameters.getNbn())
            .dataSupplier(parameters.getDataSupplier())
            .dataversePid(parameters.getDataversePid())
            .dataversePidVersion(parameters.getDataversePidVersion())
            .otherId(parameters.getOtherId())
            .otherIdVersion(parameters.getOtherIdVersion())
            .ocflObjectPath(parameters.getOcflObjectPath())
            .filePidToLocalPath(parameters.getFilePidToLocalPath())
            .exportTimestamp(parameters.getExportTimestamp())
            .build();

        result.setMetadata(parameters.getMetadata());

        return result;
    }
}
