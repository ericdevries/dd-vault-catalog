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
package nl.knaw.dans.catalog.core.domain;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.Map;

@Value
@Builder
public class OcflObjectVersionParameters {
    String swordToken;
    String nbn;
    String dataSupplier;
    String dataversePid;
    String dataversePidVersion;
    String otherId;
    String otherIdVersion;
    String ocflObjectPath;
    Map<String, Object> metadata;
    String filePidToLocalPath;
    OffsetDateTime exportTimestamp;
    Boolean skeletonRecord;
}
