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
package nl.knaw.dans.catalog.resource.mappers;

import nl.knaw.dans.catalog.api.CreateOcflObjectVersionRequestDto;
import nl.knaw.dans.catalog.api.OcflObjectVersionDto;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersionParameters;
import nl.knaw.dans.catalog.db.OcflObjectVersionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface OcflObjectVersionMapper {
    OcflObjectVersionMapper INSTANCE = Mappers.getMapper(OcflObjectVersionMapper.class);

    OcflObjectVersionParameters convert(CreateOcflObjectVersionRequestDto versionDto);

    @Mapping(source = "tar.tarUuid", target = "tarUuid")
    OcflObjectVersionDto convert(OcflObjectVersionEntity version);

    default UUID mapUuid(String value) {
        if (value == null) {
            return null;
        }
        return UUID.fromString(value);
    }

    default String mapUuid(UUID value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    default String mapDefaultObject(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }
}
