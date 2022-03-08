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

package nl.knaw.dans.catalog.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class TarOld {

    @NotEmpty
    @JsonProperty("tar_uuid")
    private String tarUuid;

    @NotEmpty
    @JsonProperty("vault_path")
    private String vaultPath;

    @NotNull
    @JsonProperty("archival_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING) //, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private OffsetDateTime archivalDate;

    @Valid
    @NotEmpty
    @JsonProperty("tar_parts")
    private List<TarPartOld> tarParts = new ArrayList<>();

    @Valid
    @NotEmpty
    @JsonProperty("transfer_items")
    private List<TransferItemOld> transferItems = new ArrayList<>();

    public List<TarPartOld> getTarParts() {
        return tarParts;
    }

    public void setTarParts(List<TarPartOld> tarParts) {
        this.tarParts = tarParts;
    }

    public List<TransferItemOld> getTransferItems() {
        return transferItems;
    }

    public void setTransferItems(List<TransferItemOld> transferItems) {
        this.transferItems = transferItems;
    }

    public String getVaultPath() {
        return vaultPath;
    }

    public void setVaultPath(String vaultPath) {
        this.vaultPath = vaultPath;
    }

    public String getTarUuid() {
        return tarUuid;
    }

    public void setTarUuid(String tarUuid) {
        this.tarUuid = tarUuid;
    }

    public OffsetDateTime getArchivalDate() {
        return archivalDate;
    }

    public void setArchivalDate(OffsetDateTime archivalDate) {
        this.archivalDate = archivalDate;
    }

    @Override
    public String toString() {
        return "Tar{" +
            "tarUuid='" + tarUuid + '\'' +
            ", vaultPath='" + vaultPath + '\'' +
            ", archivalDate=" + archivalDate +
            ", tarParts=" + tarParts +
            ", transferItems=" + transferItems +
            '}';
    }
}
