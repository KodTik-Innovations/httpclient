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

package org.kodtik.ide.http.impl.conn;

import org.kodtik.ide.http.HttpException;
import org.kodtik.ide.http.HttpHost;
import org.kodtik.ide.http.HttpRequest;
import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.conn.SchemePortResolver;
import org.kodtik.ide.http.protocol.HttpContext;
import org.kodtik.ide.http.util.Args;

/**
 * Implementation of an {@link org.kodtik.ide.http.conn.routing.HttpRoutePlanner}
 * that routes requests through a default proxy.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultProxyRoutePlanner extends DefaultRoutePlanner {

    private final HttpHost proxy;

    public DefaultProxyRoutePlanner(final HttpHost proxy, final SchemePortResolver schemePortResolver) {
        super(schemePortResolver);
        this.proxy = Args.notNull(proxy, "Proxy host");
    }

    public DefaultProxyRoutePlanner(final HttpHost proxy) {
        this(proxy, null);
    }

    @Override
    protected HttpHost determineProxy(
        final HttpHost target,
        final HttpRequest request,
        final HttpContext context) throws HttpException {
        return proxy;
    }

}
