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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class TransferItem {
    @NotEmpty
    @JsonProperty("bag_id")
    private String bagId;
    @NotEmpty
    @JsonProperty("object_version")
    private String objectVersion;
    @NotEmpty
    @JsonProperty("datastation")
    private String datastation;
    @NotEmpty
    @JsonProperty("dataverse_pid")
    private String dataversePid;
    @NotEmpty
    @JsonProperty("dataverse_pid_version")
    private String dataversePidVersion;
    @NotEmpty
    @JsonProperty("nbn")
    private String nbn;
    @JsonProperty("other_id")
    private String otherId;
    @JsonProperty("other_id_version")
    private String otherIdVersion;
    @NotEmpty
    @JsonProperty("sword_client")
    private String swordClient;
    @JsonProperty("sword_token")
    private String swordToken;
    @NotEmpty
    @JsonProperty("ocfl_object_path")
    private String ocflObjectPath;
    @NotNull
    @JsonProperty("metadata")
    private JsonNode metadata;
    @JsonProperty("filepid_to_local_path")
    private String filepidToLocalPath; // what is this?

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

    public JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonNode metadata) {
        this.metadata = metadata;
    }

    public String getFilepidToLocalPath() {
        return filepidToLocalPath;
    }

    public void setFilepidToLocalPath(String filepidToLocalPath) {
        this.filepidToLocalPath = filepidToLocalPath;
    }

    @Override
    public String toString() {
        return "TransferItem{" +
            "bagId='" + bagId + '\'' +
            ", objectVersion='" + objectVersion + '\'' +
            ", datastation='" + datastation + '\'' +
            ", dataversePid='" + dataversePid + '\'' +
            ", dataversePidVersion='" + dataversePidVersion + '\'' +
            ", nbn='" + nbn + '\'' +
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
