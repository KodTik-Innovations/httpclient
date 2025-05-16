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

import java.net.InetAddress;

import org.kodtik.ide.http.HttpException;
import org.kodtik.ide.http.HttpHost;
import org.kodtik.ide.http.HttpRequest;
import org.kodtik.ide.http.ProtocolException;
import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.client.config.RequestConfig;
import org.kodtik.ide.http.client.protocol.HttpClientContext;
import org.kodtik.ide.http.conn.SchemePortResolver;
import org.kodtik.ide.http.conn.UnsupportedSchemeException;
import org.kodtik.ide.http.conn.routing.HttpRoute;
import org.kodtik.ide.http.conn.routing.HttpRoutePlanner;
import org.kodtik.ide.http.protocol.HttpContext;
import org.kodtik.ide.http.util.Args;

/**
 * Default implementation of an {@link HttpRoutePlanner}. It will not make use of
 * any Java system properties, nor of system or browser proxy settings.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultRoutePlanner implements HttpRoutePlanner {

    private final SchemePortResolver schemePortResolver;

    public DefaultRoutePlanner(final SchemePortResolver schemePortResolver) {
        super();
        this.schemePortResolver = schemePortResolver != null ? schemePortResolver :
            DefaultSchemePortResolver.INSTANCE;
    }

    @Override
    public HttpRoute determineRoute(
            final HttpHost host,
            final HttpRequest request,
            final HttpContext context) throws HttpException {
        Args.notNull(request, "Request");
        if (host == null) {
            throw new ProtocolException("Target host is not specified");
        }
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig config = clientContext.getRequestConfig();
        final InetAddress local = config.getLocalAddress();
        HttpHost proxy = config.getProxy();
        if (proxy == null) {
            proxy = determineProxy(host, request, context);
        }

        final HttpHost target;
        if (host.getPort() <= 0) {
            try {
                target = new HttpHost(
                        host.getHostName(),
                        this.schemePortResolver.resolve(host),
                        host.getSchemeName());
            } catch (final UnsupportedSchemeException ex) {
                throw new HttpException(ex.getMessage());
            }
        } else {
            target = host;
        }
        final boolean secure = target.getSchemeName().equalsIgnoreCase("https");
        return proxy == null
                        ? new HttpRoute(target, local, secure)
                        : new HttpRoute(target, local, proxy, secure);
    }

    /**
     * This implementation returns null.
     *
     * @throws HttpException may be thrown if overridden
     */
    protected HttpHost determineProxy(
            final HttpHost target,
            final HttpRequest request,
            final HttpContext context) throws HttpException {
        return null;
    }

}
