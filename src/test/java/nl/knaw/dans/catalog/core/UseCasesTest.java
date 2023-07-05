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
import nl.knaw.dans.catalog.core.domain.TarParameters;
import nl.knaw.dans.catalog.core.exception.OcflObjectVersionAlreadyInTarException;
import nl.knaw.dans.catalog.core.exception.TarAlreadyExistsException;
import nl.knaw.dans.catalog.db.OcflObjectVersion;
import nl.knaw.dans.catalog.db.Tar;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UseCasesTest {


    @Test
    void createTar_should_throw_TarAlreadyExistsException_when_tar_already_exists() {
        var ocflObjectRepo = Mockito.mock(OcflObjectVersionRepository.class);
        var tarRepo = Mockito.mock(TarRepository.class);
        var searchIndex = Mockito.mock(SearchIndex.class);
        var usecases = new UseCases(ocflObjectRepo, tarRepo, searchIndex);

        Mockito.doReturn(Optional.of(Tar.builder().tarUuid("fake-id").build()))
                .when(tarRepo).getTarById(Mockito.eq("fake-id"));

        assertThrows(TarAlreadyExistsException.class, () -> usecases.createTar("fake-id", TarParameters.builder()
                .vaultPath("path/1")
                .build())
        );
    }

    @Test
    void createTar_should_throw_OcflObjectVersionAlreadyInTarException_if_versions_belong_to_another_tar() throws Exception {
        var ocflObjectRepo = Mockito.mock(OcflObjectVersionRepository.class);
        var tarRepo = Mockito.mock(TarRepository.class);
        var searchIndex = Mockito.mock(SearchIndex.class);
        var usecases = new UseCases(ocflObjectRepo, tarRepo, searchIndex);

        var ocflObjectVersion = OcflObjectVersion.builder()
                .bagId("bagid")
                .objectVersion(1)
                .tar(Tar.builder().tarUuid("another-tar").build())
                .build();

        Mockito.doReturn(List.of(ocflObjectVersion))
                .when(ocflObjectRepo).findAll(Mockito.any());

        assertThrows(OcflObjectVersionAlreadyInTarException.class, () ->
                usecases.createTar("fake-id", TarParameters.builder()
                        .vaultPath("path/1")
                        .versions(List.of(new OcflObjectVersionId("bagid", 1)))
                        .build()
                )
        );
    }

    @Test
    void updateTar_should_not_throw_OcflObjectVersionAlreadyInTarException_if_version_belongs_to_same_tar() throws Exception {
        var ocflObjectRepo = Mockito.mock(OcflObjectVersionRepository.class);
        var tarRepo = Mockito.mock(TarRepository.class);
        var searchIndex = Mockito.mock(SearchIndex.class);
        var usecases = new UseCases(ocflObjectRepo, tarRepo, searchIndex);
        var tar = Tar.builder().tarUuid("fake-id").tarParts(new ArrayList<>()).build();

        var ocflObjectVersion = OcflObjectVersion.builder()
                .bagId("bagid")
                .objectVersion(1)
                .tar(tar)
                .build();

        Mockito.doReturn(Optional.of(tar))
                .when(tarRepo).getTarById(Mockito.eq("fake-id"));

        Mockito.doReturn(List.of(ocflObjectVersion))
                .when(ocflObjectRepo).findAll(Mockito.any());

        assertDoesNotThrow(() ->
                usecases.updateTar("fake-id", TarParameters.builder()
                        .vaultPath("path/1")
                        .tarParts(new ArrayList<>())
                        .versions(List.of(new OcflObjectVersionId("bagid", 1)))
                        .build()
                )
        );
    }
}