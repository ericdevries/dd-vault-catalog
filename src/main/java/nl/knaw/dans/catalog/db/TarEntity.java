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

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@Getter
@Setter
@Entity
@Table(name = "tars")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarEntity {
    @Id
    @Column(name = "tar_uuid", nullable = false)
    private String tarUuid;
    @Column(name = "vault_path")
    private String vaultPath;
    @Column(name = "archival_date")
    private OffsetDateTime archivalDate;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tar")
    @ToString.Exclude
    private List<TarPartEntity> tarParts = new ArrayList<>();
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "tar")
    @ToString.Exclude
    private List<OcflObjectVersionEntity> ocflObjectVersions = new ArrayList<>();

    public List<TarPartEntity> getTarParts() {
        return new ArrayList<>(tarParts);
    }

    public void setTarParts(List<TarPartEntity> tarParts) {
        if (this.tarParts == null) {
            this.tarParts = new ArrayList<>();
        }

        this.tarParts.clear();

        for (var part : tarParts) {
            part.setTar(this);
            this.tarParts.add(part);
        }
    }

    public List<OcflObjectVersionEntity> getOcflObjectVersions() {
        return new ArrayList<>(ocflObjectVersions);
    }

    public void setOcflObjectVersions(List<OcflObjectVersionEntity> ocflObjectVersions) {
        if (this.ocflObjectVersions == null) {
            this.ocflObjectVersions = new ArrayList<>();
        }

        for (var version : this.ocflObjectVersions) {
            version.setTar(null);
        }

        this.ocflObjectVersions.clear();

        for (var version : ocflObjectVersions) {
            version.setTar(this);
            this.ocflObjectVersions.add(version);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TarEntity tarEntity = (TarEntity) o;
        return getTarUuid() != null && Objects.equals(getTarUuid(), tarEntity.getTarUuid());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
