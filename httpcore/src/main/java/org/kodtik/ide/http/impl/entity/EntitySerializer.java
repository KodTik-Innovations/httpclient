/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.kodtik.ide.http.impl.entity;

import java.io.IOException;
import java.io.OutputStream;

import org.kodtik.ide.http.HttpEntity;
import org.kodtik.ide.http.HttpException;
import org.kodtik.ide.http.HttpMessage;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.entity.ContentLengthStrategy;
import org.kodtik.ide.http.impl.io.ChunkedOutputStream;
import org.kodtik.ide.http.impl.io.ContentLengthOutputStream;
import org.kodtik.ide.http.impl.io.IdentityOutputStream;
import org.kodtik.ide.http.io.SessionOutputBuffer;
import org.kodtik.ide.http.util.Args;

/**
 * HTTP entity serializer.
 * <p>
 * This entity serializer currently supports "chunked" and "identitiy"
 * transfer-coding and content length delimited content.
 * <p>
 * This class relies on a specific implementation of
 * {@link ContentLengthStrategy} to determine the content length or transfer
 * encoding of the entity.
 * <p>
 * This class writes out the content of {@link HttpEntity} to the data stream
 * using a transfer coding based on properties on the HTTP message.
 *
 * @since 4.0
 *
 * @deprecated (4.3) use {@link org.kodtik.ide.http.impl.BHttpConnectionBase}
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Deprecated
public class EntitySerializer {

    private final ContentLengthStrategy lenStrategy;

    public EntitySerializer(final ContentLengthStrategy lenStrategy) {
        super();
        this.lenStrategy = Args.notNull(lenStrategy, "Content length strategy");
    }

    /**
     * Creates a transfer codec based on properties of the given HTTP message
     * and returns {@link OutputStream} instance that transparently encodes
     * output data as it is being written out to the output stream.
     * <p>
     * This method is called by the public
     * {@link #serialize(SessionOutputBuffer, HttpMessage, HttpEntity)}.
     *
     * @param outbuffer the session output buffer.
     * @param message the HTTP message.
     * @return output stream.
     * @throws HttpException in case of HTTP protocol violation.
     * @throws IOException in case of an I/O error.
     */
    protected OutputStream doSerialize(
            final SessionOutputBuffer outbuffer,
            final HttpMessage message) throws HttpException, IOException {
        final long len = this.lenStrategy.determineLength(message);
        if (len == ContentLengthStrategy.CHUNKED) {
            return new ChunkedOutputStream(outbuffer);
        } else if (len == ContentLengthStrategy.IDENTITY) {
            return new IdentityOutputStream(outbuffer);
        } else {
            return new ContentLengthOutputStream(outbuffer, len);
        }
    }

    /**
     * Writes out the content of the given HTTP entity to the session output
     * buffer based on properties of the given HTTP message.
     *
     * @param outbuffer the output session buffer.
     * @param message the HTTP message.
     * @param entity the HTTP entity to be written out.
     * @throws HttpException in case of HTTP protocol violation.
     * @throws IOException in case of an I/O error.
     */
    public void serialize(
            final SessionOutputBuffer outbuffer,
            final HttpMessage message,
            final HttpEntity entity) throws HttpException, IOException {
        Args.notNull(outbuffer, "Session output buffer");
        Args.notNull(message, "HTTP message");
        Args.notNull(entity, "HTTP entity");
        final OutputStream outStream = doSerialize(outbuffer, message);
        entity.writeTo(outStream);
        outStream.close();
    }

}
