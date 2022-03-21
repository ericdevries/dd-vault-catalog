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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tars")
public class Tar {
    @Id
    @Column(name = "tar_uuid", nullable = false)
    private String tarUuid;
    @Column(name = "vault_path")
    private String vaultPath;
    @Column(name = "archival_date")
    private OffsetDateTime archivalDate;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tar")
    private List<TarPart> tarParts = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tar")
    private List<OcflObjectVersion> ocflObjectVersions = new ArrayList<>();

    public Tar() {

    }

    public Tar(String tarUuid, String vaultPath, OffsetDateTime archivalDate) {
        this.tarUuid = tarUuid;
        this.vaultPath = vaultPath;
        this.archivalDate = archivalDate;
    }

    public String getTarUuid() {
        return tarUuid;
    }

    public void setTarUuid(String tarUuid) {
        this.tarUuid = tarUuid;
    }

    public String getVaultPath() {
        return vaultPath;
    }

    public void setVaultPath(String vaultPath) {
        this.vaultPath = vaultPath;
    }

    public OffsetDateTime getArchivalDate() {
        return archivalDate;
    }

    public void setArchivalDate(OffsetDateTime archivalDate) {
        this.archivalDate = archivalDate;
    }

    public List<TarPart> getTarParts() {
        return tarParts;
    }

    public void setTarParts(List<TarPart> tarParts) {
        this.tarParts = tarParts;
    }

    public List<OcflObjectVersion> getOcflObjectVersions() {
        return ocflObjectVersions;
    }

    public void setOcflObjectVersions(List<OcflObjectVersion> ocflObjectVersions) {
        this.ocflObjectVersions = ocflObjectVersions;
    }

    @Override
    public String toString() {
        return "Tar{" +
            "tarUuid='" + tarUuid + '\'' +
            ", vaultPath='" + vaultPath + '\'' +
            ", archivalDate=" + archivalDate +
            ", tarParts=" + tarParts +
            ", ocflObjectVersions=" + ocflObjectVersions +
            '}';
    }
}
