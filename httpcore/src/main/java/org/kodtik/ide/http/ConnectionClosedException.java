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

package org.kodtik.ide.http;

import java.io.IOException;

/**
 * Signals that the connection has been closed unexpectedly.
 *
 * @since 4.0
 */
public class ConnectionClosedException extends IOException {

    private static final long serialVersionUID = 617550366255636674L;

    /**
     * Creates a new ConnectionClosedException with the message "Connection is closed".
     *
     * @since 4.4.11
     */
    public ConnectionClosedException() {
        super("Connection is closed");
    }

    /**
     * Creates a new ConnectionClosedException with the specified detail message.
     *
     * @param message The exception detail message
     */
    public ConnectionClosedException(final String message) {
        super(HttpException.clean(message));
    }

    /**
     * Constructs a new ConnectionClosedException with the specified detail message.
     *
     * @param format The exception detail message format; see {@link String#format(String, Object...)}.
     * @param args The exception detail message arguments; see {@link String#format(String, Object...)}.
     *
     * @since 4.4.11
     */
    public ConnectionClosedException(final String format, final Object... args) {
        super(HttpException.clean(String.format(format, args)));
    }

}
