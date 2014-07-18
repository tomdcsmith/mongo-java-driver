/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.protocol.message;

import com.mongodb.operation.BaseUpdateRequest;
import com.mongodb.operation.ReplaceRequest;
import org.bson.codecs.Encoder;
import org.bson.io.OutputBuffer;

import java.util.List;

public class ReplaceMessage<T> extends BaseUpdateMessage {
    private final List<ReplaceRequest<T>> replaceRequests;
    private final Encoder<T> encoder;

    public ReplaceMessage(final String collectionName, final List<ReplaceRequest<T>> replaceRequests,
                          final Encoder<T> encoder, final MessageSettings settings) {
        super(collectionName, OpCode.OP_UPDATE, settings);
        this.replaceRequests = replaceRequests;
        this.encoder = encoder;
    }

    @Override
    protected RequestMessage encodeMessageBody(final OutputBuffer buffer, final int messageStartPosition) {
        writeBaseUpdate(buffer);
        addCollectibleDocument(replaceRequests.get(0).getReplacement(), encoder, buffer, new CollectibleDocumentFieldNameValidator());
        if (replaceRequests.size() == 1) {
            return null;
        } else {
            return new ReplaceMessage<T>(getCollectionName(), replaceRequests.subList(1, replaceRequests.size()), encoder, getSettings());
        }
    }

    @Override
    protected BaseUpdateRequest getUpdateBase() {
        return replaceRequests.get(0);
    }
}
