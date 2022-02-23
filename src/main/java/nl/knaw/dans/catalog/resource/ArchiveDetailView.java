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

package nl.knaw.dans.catalog.resource;

import io.dropwizard.views.View;
import nl.knaw.dans.catalog.db.TransferItemModel;

public class ArchiveDetailView extends View {
    private final TransferItemModel transferItem;

    protected ArchiveDetailView(TransferItemModel transferItem) {
        super("transfer-item.ftl");
        this.transferItem = transferItem;
    }

    public TransferItemModel getTransferItem() {
        return transferItem;
    }
}
