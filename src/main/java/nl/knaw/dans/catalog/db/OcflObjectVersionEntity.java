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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersion;
import nl.knaw.dans.catalog.core.domain.OcflObjectVersionId;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "ocfl_object_versions")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OcflObjectVersionEntity implements OcflObjectVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "bag_id", nullable = false)
    private String bagId;
    @Column(name = "object_version", nullable = false)
    private Integer objectVersion;
    @ManyToOne
    @JoinColumn(name = "tar_uuid")
    private TarEntity tar;
    @Column(name = "data_supplier")
    private String dataSupplier;
    @Column(name = "dataverse_pid")
    private String dataversePid;
    @Column(name = "dataverse_pid_version")
    private String dataversePidVersion;
    @Column(name = "nbn")
    private String nbn;
    @Column(name = "other_id")
    private String otherId;
    @Column(name = "other_id_version")
    private String otherIdVersion;
    @Column(name = "sword_client")
    private String swordClient;
    @Column(name = "sword_token")
    private String swordToken;
    @Column(name = "ocfl_object_path")
    private String ocflObjectPath;
    @Lob
    @Column(name = "metadata")
    private String metadata;
    @Lob
    @Column(name = "filepid_to_local_path")
    private String filePidToLocalPath;
    @Column(name = "export_timestamp")
    private OffsetDateTime exportTimestamp;

    private ObjectMapper objectMapper;

    public OcflObjectVersionId getId() {
        return new OcflObjectVersionId(bagId, objectVersion);
    }

    @Override
    public String getMetadataString() {
        return metadata;
    }

    @Override
    // TODO this is not efficient
    public Map<String, Object> getMetadata() {
        try {
            return new ObjectMapper().readValue(metadata, new TypeReference<>() {
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }

    void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata == null ? null : new ObjectMapper().valueToTree(metadata).toString();
    }


    @Override
    public OffsetDateTime getExportTimestamp() {
        return exportTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OcflObjectVersionEntity that = (OcflObjectVersionEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
