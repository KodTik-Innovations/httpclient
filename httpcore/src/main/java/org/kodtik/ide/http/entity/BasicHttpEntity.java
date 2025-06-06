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

package org.kodtik.ide.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.kodtik.ide.http.impl.io.EmptyInputStream;
import org.kodtik.ide.http.util.Args;
import org.kodtik.ide.http.util.Asserts;

/**
 * A generic streamed, non-repeatable entity that obtains its content
 * from an {@link InputStream}.
 *
 * @since 4.0
 */
public class BasicHttpEntity extends AbstractHttpEntity {

    private InputStream content;
    private long length;

    /**
     * Creates a new basic entity.
     * The content is initially missing, the content length
     * is set to a negative number.
     */
    public BasicHttpEntity() {
        super();
        this.length = -1;
    }

    @Override
    public long getContentLength() {
        return this.length;
    }

    /**
     * Obtains the content, once only.
     *
     * @return  the content, if this is the first call to this method
     *          since {@link #setContent setContent} has been called
     *
     * @throws IllegalStateException
     *          if the content has not been provided
     */
    @Override
    public InputStream getContent() throws IllegalStateException {
        Asserts.check(this.content != null, "Content has not been provided");
        return this.content;
    }

    /**
     * Tells that this entity is not repeatable.
     *
     * @return {@code false}
     */
    @Override
    public boolean isRepeatable() {
        return false;
    }

    /**
     * Specifies the length of the content.
     *
     * @param len       the number of bytes in the content, or
     *                  a negative number to indicate an unknown length
     */
    public void setContentLength(final long len) {
        this.length = len;
    }

    /**
     * Specifies the content.
     *
     * @param inStream          the stream to return with the next call to
     *                          {@link #getContent getContent}
     */
    public void setContent(final InputStream inStream) {
        this.content = inStream;
    }

    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        final InputStream inStream = getContent();
        try {
            int l;
            final byte[] tmp = new byte[OUTPUT_BUFFER_SIZE];
            while ((l = inStream.read(tmp)) != -1) {
                outStream.write(tmp, 0, l);
            }
        } finally {
            inStream.close();
        }
    }

    @Override
    public boolean isStreaming() {
        return this.content != null && this.content != EmptyInputStream.INSTANCE;
    }

}
