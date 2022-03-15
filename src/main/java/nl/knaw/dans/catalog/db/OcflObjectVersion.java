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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ocfl_object_versions", uniqueConstraints = { @UniqueConstraint(columnNames = { "bag_id", "version_major", "version_minor" }) })
public class OcflObjectVersion {

    @Id
    @Column(name = "bag_id")
    private String bagId;
    @Column(name = "object_version", nullable = false)
    private String objectVersion;
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "tar_uuid", nullable = false)
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
    @Column(name = "sword_client")
    private String swordClient;
    @Column(name = "sword_token")
    private String swordToken;
    @Column(name = "ocfl_object_path", nullable = false)
    private String ocflObjectPath;
    @Column(name = "metadata", nullable = false, columnDefinition = "clob")
    private String metadata;
    @Column(name = "filepid_to_local_path", columnDefinition = "clob")
    private String filepidToLocalPath; // what is this?

    public OcflObjectVersion(String bagId, String objectVersion, Tar tar, String datastation, String dataversePid, String dataversePidVersion, String nbn, int versionMajor, int versionMinor,
        String otherId, String otherIdVersion, String swordClient, String swordToken, String ocflObjectPath, String metadata, String filepidToLocalPath) {
        this.bagId = bagId;
        this.objectVersion = objectVersion;
        this.tar = tar;
        this.datastation = datastation;
        this.dataversePid = dataversePid;
        this.dataversePidVersion = dataversePidVersion;
        this.nbn = nbn;
        this.versionMajor = versionMajor;
        this.versionMinor = versionMinor;
        this.otherId = otherId;
        this.otherIdVersion = otherIdVersion;
        this.swordClient = swordClient;
        this.swordToken = swordToken;
        this.ocflObjectPath = ocflObjectPath;
        this.metadata = metadata;
        this.filepidToLocalPath = filepidToLocalPath;
    }

    public OcflObjectVersion() {

    }

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

    @Override
    public String toString() {
        return "OcflObjectVersion{" +
            "bagId='" + bagId + '\'' +
            ", objectVersion='" + objectVersion + '\'' +
            ", datastation='" + datastation + '\'' +
            ", dataversePid='" + dataversePid + '\'' +
            ", dataversePidVersion='" + dataversePidVersion + '\'' +
            ", nbn='" + nbn + '\'' +
            ", versionMajor=" + versionMajor +
            ", versionMinor=" + versionMinor +
            ", otherId='" + otherId + '\'' +
            ", otherIdVersion='" + otherIdVersion + '\'' +
            ", swordClient='" + swordClient + '\'' +
            ", swordToken='" + swordToken + '\'' +
            ", ocflObjectPath='" + ocflObjectPath + '\'' +
            ", metadata='" + metadata + '\'' +
            ", filepidToLocalPath='" + filepidToLocalPath + '\'' +
            '}';
    }
}
