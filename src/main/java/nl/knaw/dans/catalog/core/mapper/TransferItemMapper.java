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
package nl.knaw.dans.catalog.core.mapper;

import nl.knaw.dans.catalog.db.TransferItem;
import nl.knaw.dans.catalog.openapi.api.TransferItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransferItemMapper {
    TransferItemMapper INSTANCE = Mappers.getMapper(TransferItemMapper.class);

    @Mapping(expression = "java(JsonMapper.toJson(transferItem.getMetadata()))", target = "metadata")
    TransferItemDto transferItemToTransferItemDto(TransferItem transferItem);

}

