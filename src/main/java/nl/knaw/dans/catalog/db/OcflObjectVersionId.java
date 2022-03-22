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

import javax.persistence.Column;
import java.io.Serializable;

public class OcflObjectVersionId implements Serializable {
    @Column(name = "bag_id")
    private String bagId;
    @Column(name = "version_major", nullable = false)
    private int versionMajor;
    @Column(name = "version_minor", nullable = false)
    private int versionMinor;

    public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
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

    @Override
    public String toString() {
        return "OcflObjectVersionId{" +
            "bagId='" + bagId + '\'' +
            ", versionMajor=" + versionMajor +
            ", versionMinor=" + versionMinor +
            '}';
    }
}
