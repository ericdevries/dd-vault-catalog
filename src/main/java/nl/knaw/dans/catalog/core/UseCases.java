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
import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersionId;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersionParameters;
import nl.knaw.dans.catalog.core.domain.TarParameters;
import nl.knaw.dans.catalog.core.exception.*;
import nl.knaw.dans.catalog.db.OcflObjectVersionEntity;
import nl.knaw.dans.catalog.db.TarEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class UseCases {
    private final OcflObjectVersionRepository ocflObjectVersionRepository;
    private final OcflObjectVersionFactory ocflObjectVersionFactory;
    private final TarRepository tarRepository;
    private final TarFactory tarFactory;
    private final SearchIndex searchIndex;

    public UseCases(OcflObjectVersionRepository ocflObjectVersionRepository, OcflObjectVersionFactory ocflObjectVersionFactory, TarRepository tarRepository, TarFactory tarFactory, SearchIndex searchIndex) {
        this.ocflObjectVersionRepository = ocflObjectVersionRepository;
        this.ocflObjectVersionFactory = ocflObjectVersionFactory;
        this.tarRepository = tarRepository;
        this.tarFactory = tarFactory;
        this.searchIndex = searchIndex;
    }

    public Collection<OcflObjectVersionEntity> findOcflObjectVersionByBagId(String bagId) {
        return ocflObjectVersionRepository.findAllByBagId(bagId);
    }

    public Collection<OcflObjectVersionEntity> findOcflObjectVersionBySwordToken(String swordToken) {
        return ocflObjectVersionRepository.findAllBySwordToken(swordToken);
    }

    @UnitOfWork
    public OcflObjectVersionEntity createOcflObjectVersion(OcflObjectVersionId id, OcflObjectVersionParameters params) throws OcflObjectVersionAlreadyExistsException {
        var existingOcflObjectVersion = ocflObjectVersionRepository.findByBagIdAndVersion(id.getBagId(), id.getObjectVersion());

        if (existingOcflObjectVersion.isPresent()) {
            throw new OcflObjectVersionAlreadyExistsException(id.getBagId(), id.getObjectVersion());
        }

        var ocflObjectVersion = ocflObjectVersionFactory.create(
            new OcflObjectVersionId(id.getBagId(), id.getObjectVersion()),
            params
        );

        ocflObjectVersionRepository.save(ocflObjectVersion);
        return ocflObjectVersion;
    }

    @UnitOfWork
    public TarEntity createTar(String id, TarParameters params) throws TarAlreadyExistsException, OcflObjectVersionNotFoundException, OcflObjectVersionAlreadyInTarException {
        var existingTar = tarRepository.getTarById(id);

        if (existingTar.isPresent()) {
            throw new TarAlreadyExistsException(id);
        }

        // throws exception if one cannot be found
        var ocflObjectVersions = ocflObjectVersionRepository.findAll(params.getVersions());

        // check if all ocfl object versions are not already in a tar
        for (var version : ocflObjectVersions) {
            if (version.getTar() != null) {
                throw new OcflObjectVersionAlreadyInTarException(String.format(
                    "OcflObjectVersion with bagId %s and version %d is already in TAR %s",
                    version.getId().getBagId(), version.getId().getObjectVersion(), version.getTar().getTarUuid()
                ));
            }
        }

        var tar = tarFactory.create(id, params.getVaultPath(), params.getArchivalDate(), params.getTarParts(), ocflObjectVersions);
        var result = tarRepository.save(tar);
        searchIndex.indexTar(result);
        return result;
    }

    @UnitOfWork
    public Optional<TarEntity> getTarById(String id) {
        return tarRepository.getTarById(id);
    }

    @UnitOfWork
    public TarEntity updateTar(String id, TarParameters params) throws TarNotFoundException, OcflObjectVersionNotFoundException, OcflObjectVersionAlreadyInTarException {
        var tar = tarRepository.getTarById(id)
            .orElseThrow(() -> new TarNotFoundException(
                String.format("Tar with id %s not found", id)
            ));

        var ocflObjectVersions = ocflObjectVersionRepository.findAll(params.getVersions());

        // check if all ocfl object versions are not already in a tar
        for (var version : ocflObjectVersions) {
            if (version.getTar() != null && version.getTar().equals(tar)) {
                throw new OcflObjectVersionAlreadyInTarException(String.format(
                    "OcflObjectVersion with bagId %s and version %d is already in TAR %s, cannot add to TAR %s",
                    version.getId().getBagId(), version.getId().getObjectVersion(), version.getTar().getTarUuid(), tar.getTarUuid()
                ));
            }
        }

        var parts = params.getTarParts().stream().map(tarFactory::createTarPart).collect(Collectors.toList());
        tar.setArchivalDate(params.getArchivalDate());
        tar.setVaultPath(params.getVaultPath());
        tar.setTarParts(parts);
        tar.setOcflObjectVersions(ocflObjectVersions);

        var result = tarRepository.save(tar);

        searchIndex.indexTar(tar);

        return result;
    }

    @UnitOfWork
    public OcflObjectVersionEntity getOcflObjectVersionByNbn(String nbn) throws OcflObjectVersionNotFoundException {
        return ocflObjectVersionRepository.findByNbn(nbn)
            .orElseThrow(() -> new OcflObjectVersionNotFoundException(
                String.format("OcflObjectVersion with NBN %s not found", nbn)
            ));
    }

    public void reindexAllTars() {
        var tars = tarRepository.findAll();

        log.info("Reindexing {} archives", tars.size());

        for (var tar : tars) {
            log.info("Reindexing TAR {}", tar.getTarUuid());
            searchIndex.indexTar(tar);
        }
    }
}
