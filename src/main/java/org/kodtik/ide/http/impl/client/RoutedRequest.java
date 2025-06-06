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

package org.kodtik.ide.http.impl.client;

import org.kodtik.ide.http.conn.routing.HttpRoute;

/**
 * A request with the route along which it should be sent.
 *
 * @since 4.0
 *
 * @deprecated (4.3) do not use.
 */
@Deprecated
public class RoutedRequest {

    protected final RequestWrapper request; // @NotThreadSafe
    protected final HttpRoute route; // @Contract(threading = ThreadingBehavior.IMMUTABLE)

    /**
     * Creates a new routed request.
     *
     * @param req   the request
     * @param route   the route
     */
    public RoutedRequest(final RequestWrapper req, final HttpRoute route) {
        super();
        this.request = req;
        this.route   = route;
    }

    public final RequestWrapper getRequest() {
        return request;
    }

    public final HttpRoute getRoute() {
        return route;
    }

}
