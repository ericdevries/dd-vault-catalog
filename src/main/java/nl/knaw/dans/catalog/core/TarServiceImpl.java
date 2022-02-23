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

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.catalog.api.Tar;
import nl.knaw.dans.catalog.api.TarPart;
import nl.knaw.dans.catalog.api.TransferItem;
import nl.knaw.dans.catalog.db.TarModel;
import nl.knaw.dans.catalog.db.TarModelDAO;
import nl.knaw.dans.catalog.db.TarPartModel;
import nl.knaw.dans.catalog.db.TransferItemDao;
import nl.knaw.dans.catalog.db.TransferItemModel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class TarServiceImpl implements TarService {
    private final TarModelDAO tarModelDAO;

    public TarServiceImpl(TarModelDAO tarModelDAO) {
        this.tarModelDAO = tarModelDAO;
    }

    @Override
    public Optional<Tar> get(String id) {
        return tarModelDAO.findById(id)
            .map(this::convertModelToApi);
    }

    @Override
    @UnitOfWork
    public void saveTar(Tar tar) {
        var model = convertTarToModel(tar);
        tarModelDAO.save(model);
    }

    Tar convertModelToApi(TarModel model) {
        var tar = new Tar();
        tar.setTarUuid(model.getTarUuid());
        tar.setVaultPath(model.getVaultPath());
        tar.setArchivalDate(model.getArchivalDate());

        var parts = model.getTarParts().stream().map(p -> {
            var part = new TarPart();
            part.setPartName(p.getPartName());
            part.setChecksumValue(p.getChecksumValue());
            part.setChecksumAlgorithm(p.getChecksumAlgorithm());

            return part;
        }).collect(Collectors.toList());

        var transferItems = model.getTransferItems().stream().map(i -> {
            var item = new TransferItem();
            item.setBagId(i.getBagId());
            item.setObjectVersion(i.getObjectVersion());
            item.setDatastation(i.getDatastation());
            item.setDataversePid(i.getDataversePid());
            item.setDataversePidVersion(i.getDataversePidVersion());
            item.setNbn(i.getNbn());
            item.setOtherId(i.getOtherId());
            item.setOtherIdVersion(i.getOtherIdVersion());
            item.setSwordClient(i.getSwordClient());
            item.setSwordToken(i.getSwordToken());
            item.setOcflObjectPath(i.getOcflObjectPath());
            try {
                item.setMetadata(new JsonMapper().readTree(i.getMetadata()));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            item.setFilepidToLocalPath(i.getFilepidToLocalPath());
            return item;
        }).collect(Collectors.toList());

        tar.setTarParts(parts);
        tar.setTransferItems(transferItems);

        return tar;
    }

    TarModel convertTarToModel(Tar tar) {
        var model = new TarModel();
        model.setTarUuid(tar.getTarUuid());
        model.setVaultPath(tar.getVaultPath());
        model.setArchivalDate(tar.getArchivalDate());

        var parts = tar.getTarParts().stream().map(p -> {
            var part = new TarPartModel();
            part.setPartName(p.getPartName());
            part.setChecksumValue(p.getChecksumValue());
            part.setChecksumAlgorithm(p.getChecksumAlgorithm());
            part.setTar(model);
            return part;
        }).collect(Collectors.toList());

        var transferItems = tar.getTransferItems().stream().map(i -> {
            var item = new TransferItemModel();
            item.setBagId(i.getBagId());
            item.setObjectVersion(i.getObjectVersion());
            item.setDatastation(i.getDatastation());
            item.setDataversePid(i.getDataversePid());
            item.setDataversePidVersion(i.getDataversePidVersion());
            item.setNbn(i.getNbn());
            item.setOtherId(i.getOtherId());
            item.setOtherIdVersion(i.getOtherIdVersion());
            item.setSwordClient(i.getSwordClient());
            item.setSwordToken(i.getSwordToken());
            item.setOcflObjectPath(i.getOcflObjectPath());
            item.setMetadata(i.getMetadata().toString().getBytes(StandardCharsets.UTF_8));
            item.setFilepidToLocalPath(i.getFilepidToLocalPath());
            item.setTar(model);
            return item;
        }).collect(Collectors.toList());

        model.setTarParts(parts);
        model.setTransferItems(transferItems);

        return model;
    }
}
