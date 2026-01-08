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

package org.kodtik.innovations.http.client.methods;

import org.kodtik.innovations.http.conn.ClientConnectionRequest;
import org.kodtik.innovations.http.conn.ConnectionReleaseTrigger;

import java.io.IOException;


/**
 * Interface representing an HTTP request that can be aborted by shutting
 * down the underlying HTTP connection.
 *
 * @since 4.0
 *
 * @deprecated (4.3) use {@link HttpExecutionAware}
 */
@Deprecated
public interface AbortableHttpRequest {

    /**
     * Sets the {@link org.kodtik.innovations.http.conn.ClientConnectionRequest}
     * callback that can be used to abort a long-lived request for a connection.
     * If the request is already aborted, throws an {@link IOException}.
     *
     * @see org.kodtik.innovations.http.conn.ClientConnectionManager
     */
    void setConnectionRequest(ClientConnectionRequest connRequest) throws IOException;

    /**
     * Sets the {@link ConnectionReleaseTrigger} callback that can
     * be used to abort an active connection.
     * Typically, this will be the
     *   {@link org.kodtik.innovations.http.conn.ManagedClientConnection} itself.
     * If the request is already aborted, throws an {@link IOException}.
     */
    void setReleaseTrigger(ConnectionReleaseTrigger releaseTrigger) throws IOException;

    /**
     * Aborts this http request. Any active execution of this method should
     * return immediately. If the request has not started, it will abort after
     * the next execution. Aborting this request will cause all subsequent
     * executions with this request to fail.
     *
     * @see org.kodtik.innovations.http.client.HttpClient#execute(HttpUriRequest)
     * @see org.kodtik.innovations.http.client.HttpClient#execute(org.kodtik.innovations.http.HttpHost,
     *      org.kodtik.innovations.http.HttpRequest)
     * @see org.kodtik.innovations.http.client.HttpClient#execute(HttpUriRequest,
     *      org.kodtik.innovations.http.protocol.HttpContext)
     * @see org.kodtik.innovations.http.client.HttpClient#execute(org.kodtik.innovations.http.HttpHost,
     *      org.kodtik.innovations.http.HttpRequest, org.kodtik.innovations.http.protocol.HttpContext)
     */
    void abort();

}

