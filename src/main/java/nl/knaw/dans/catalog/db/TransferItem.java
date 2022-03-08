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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "transfer_items", uniqueConstraints = { @UniqueConstraint(columnNames = { "bag_id", "version_major", "version_minor" }) })
@TypeDef(
    name = "json",
    typeClass = JsonType.class
)
public class TransferItem {

    @Id
    @Column(name = "bag_id")
    private String bagId;
    @Column(name = "object_version", nullable = false)
    private String objectVersion;
    @ManyToOne
    @JoinColumn(name = "tar_uuid")
    private Tar tar;
    @Column(name = "datastation", nullable = false)
    private String datastation;
    @Column(name = "dataverse_pid", nullable = false)
    private String dataversePid;
    @Column(name = "dataverse_pid_version", nullable = false)
    private String dataversePidVersion;
    @Column(name = "nbn", nullable = false)
    private String nbn;
    @Column(name = "version_major", nullable = false)
    private int versionMajor;
    @Column(name = "version_minor", nullable = false)
    private int versionMinor;
    @Column(name = "other_id")
    private String otherId;
    @Column(name = "other_id_version")
    private String otherIdVersion;
    @Column(name = "sword_client", nullable = false)
    private String swordClient;
    @Column(name = "sword_token")
    private String swordToken;
    @Column(name = "ocfl_object_path", nullable = false)
    private String ocflObjectPath;
    @Column(name = "metadata", nullable = false, length = 100000)
    @Type(type = "json")
    private String metadata;
    @Column(name = "filepid_to_local_path")
    private String filepidToLocalPath; // what is this?

    public int getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor(int versionMajor) {
        this.versionMajor = versionMajor;
    }

    public int getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor(int versionMinor) {
        this.versionMinor = versionMinor;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getFilepidToLocalPath() {
        return filepidToLocalPath;
    }

    public void setFilepidToLocalPath(String filepidToLocalPath) {
        this.filepidToLocalPath = filepidToLocalPath;
    }

    public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }

    public String getObjectVersion() {
        return objectVersion;
    }

    public void setObjectVersion(String objectVersion) {
        this.objectVersion = objectVersion;
    }

    public Tar getTar() {
        return tar;
    }

    public void setTar(Tar tar) {
        this.tar = tar;
    }

    public String getDatastation() {
        return datastation;
    }

    public void setDatastation(String datastation) {
        this.datastation = datastation;
    }

    public String getDataversePid() {
        return dataversePid;
    }

    public void setDataversePid(String dataversePid) {
        this.dataversePid = dataversePid;
    }

    public String getDataversePidVersion() {
        return dataversePidVersion;
    }

    public void setDataversePidVersion(String dataversePidVersion) {
        this.dataversePidVersion = dataversePidVersion;
    }

    public String getNbn() {
        return nbn;
    }

    public void setNbn(String nbn) {
        this.nbn = nbn;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String getOtherIdVersion() {
        return otherIdVersion;
    }

    public void setOtherIdVersion(String otherIdVersion) {
        this.otherIdVersion = otherIdVersion;
    }

    public String getSwordClient() {
        return swordClient;
    }

    public void setSwordClient(String swordClient) {
        this.swordClient = swordClient;
    }

    public String getSwordToken() {
        return swordToken;
    }

    public void setSwordToken(String swordToken) {
        this.swordToken = swordToken;
    }

    public String getOcflObjectPath() {
        return ocflObjectPath;
    }

    public void setOcflObjectPath(String ocflObjectPath) {
        this.ocflObjectPath = ocflObjectPath;
    }

    public Object getMetadataJson() {
        try {
            return new ObjectMapper().readTree(metadata);
        }
        catch (JsonProcessingException e) {
            return null;
        }
    }

    public void setMetadataJson(Object value) {
        try {
            setMetadata(new ObjectMapper().writeValueAsString(value));
        }
        catch (JsonProcessingException e) {

        }
    }
}
