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
import nl.knaw.dans.catalog.db.mappers.TarEntityMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class UseCases {
    private final OcflObjectVersionRepository ocflObjectVersionRepository;
    private final TarRepository tarRepository;
    private final SearchIndex searchIndex;

    private final TarEntityMapper tarEntityMapper = TarEntityMapper.INSTANCE;

    public UseCases(OcflObjectVersionRepository ocflObjectVersionRepository, TarRepository tarRepository, SearchIndex searchIndex) {
        this.ocflObjectVersionRepository = ocflObjectVersionRepository;
        this.tarRepository = tarRepository;
        this.searchIndex = searchIndex;
    }

    @UnitOfWork
    public Collection<OcflObjectVersionEntity> findOcflObjectVersionsByBagId(String bagId) {
        return ocflObjectVersionRepository.findAllByBagId(bagId);
    }

    @UnitOfWork
    public Collection<OcflObjectVersionEntity> findOcflObjectVersionsBySwordToken(String swordToken) {
        return ocflObjectVersionRepository.findAllBySwordToken(swordToken);
    }

    @UnitOfWork
    public List<OcflObjectVersionEntity> findOcflObjectVersionsByNbn(String nbn) throws OcflObjectVersionNotFoundException {
        var results = ocflObjectVersionRepository.findByNbn(nbn);

        if (results.size() == 0) {
            throw new OcflObjectVersionNotFoundException(
                String.format("No OCFL object versions found for NBN %s", nbn)
            );
        }

        return results;
    }

    @UnitOfWork
    public Optional<OcflObjectVersionEntity> findOcflObjectVersionByBagIdAndVersion(String bagId, Integer versionNumber) {
        return ocflObjectVersionRepository.findByBagIdAndVersion(bagId, versionNumber);
    }

    @UnitOfWork
    public OcflObjectVersionEntity createOcflObjectVersion(OcflObjectVersionId id, OcflObjectVersionParameters parameters) throws OcflObjectVersionAlreadyExistsException {
        var existingOcflObjectVersion = ocflObjectVersionRepository.findByBagIdAndVersion(id.getBagId(), id.getObjectVersion());

        if (existingOcflObjectVersion.isPresent()) {
            throw new OcflObjectVersionAlreadyExistsException(id.getBagId(), id.getObjectVersion());
        }

        var ocflObjectVersion = OcflObjectVersionEntity.builder()
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

        return ocflObjectVersionRepository.save(ocflObjectVersion);
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

        var tar = tarEntityMapper.convert(params);
        tar.setTarUuid(id);
        tar.setOcflObjectVersions(ocflObjectVersions);

        var result = tarRepository.save(tar);
        searchIndex.indexTar(result);

        return result;
    }

    @UnitOfWork
    public Optional<TarEntity> findTarById(String id) {
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

        var parts = params.getTarParts().stream().map(tarEntityMapper::convert).collect(Collectors.toList());
        tar.setArchivalDate(params.getArchivalDate());
        tar.setVaultPath(params.getVaultPath());
        tar.setTarParts(parts);
        tar.setOcflObjectVersions(ocflObjectVersions);

        var result = tarRepository.save(tar);

        searchIndex.indexTar(result);

        return result;
    }


    @UnitOfWork
    public void reindexAllTars() {
        var tars = tarRepository.findAll();

        log.info("Reindexing {} archives", tars.size());

        for (var tar : tars) {
            log.info("Reindexing TAR {}", tar.getTarUuid());
            searchIndex.indexTar(tar);
        }
    }

}
