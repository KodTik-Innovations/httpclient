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

package org.kodtik.ide.http.client.protocol;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kodtik.ide.http.HttpException;
import org.kodtik.ide.http.HttpHost;
import org.kodtik.ide.http.HttpRequest;
import org.kodtik.ide.http.HttpRequestInterceptor;
import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.auth.AuthProtocolState;
import org.kodtik.ide.http.auth.AuthScheme;
import org.kodtik.ide.http.auth.AuthScope;
import org.kodtik.ide.http.auth.AuthState;
import org.kodtik.ide.http.auth.Credentials;
import org.kodtik.ide.http.client.AuthCache;
import org.kodtik.ide.http.client.CredentialsProvider;
import org.kodtik.ide.http.conn.routing.RouteInfo;
import org.kodtik.ide.http.protocol.HttpContext;
import org.kodtik.ide.http.util.Args;

/**
 * Request interceptor that can preemptively authenticate against known hosts,
 * if there is a cached {@link AuthScheme} instance in the local
 * {@link AuthCache} associated with the given target or proxy host.
 *
 * @since 4.1
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestAuthCache implements HttpRequestInterceptor {

    private final Log log = LogFactory.getLog(getClass());

    public RequestAuthCache() {
        super();
    }

    @Override
    public void process(final HttpRequest request, final HttpContext context)
            throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");

        final HttpClientContext clientContext = HttpClientContext.adapt(context);

        final AuthCache authCache = clientContext.getAuthCache();
        if (authCache == null) {
            this.log.debug("Auth cache not set in the context");
            return;
        }

        final CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
        if (credsProvider == null) {
            this.log.debug("Credentials provider not set in the context");
            return;
        }

        final RouteInfo route = clientContext.getHttpRoute();
        if (route == null) {
            this.log.debug("Route info not set in the context");
            return;
        }

        HttpHost target = clientContext.getTargetHost();
        if (target == null) {
            this.log.debug("Target host not set in the context");
            return;
        }

        if (target.getPort() < 0) {
            target = new HttpHost(
                    target.getHostName(),
                    route.getTargetHost().getPort(),
                    target.getSchemeName());
        }

        final AuthState targetState = clientContext.getTargetAuthState();
        if (targetState != null && targetState.getState() == AuthProtocolState.UNCHALLENGED) {
            final AuthScheme authScheme = authCache.get(target);
            if (authScheme != null) {
                doPreemptiveAuth(target, authScheme, targetState, credsProvider);
            }
        }

        final HttpHost proxy = route.getProxyHost();
        final AuthState proxyState = clientContext.getProxyAuthState();
        if (proxy != null && proxyState != null && proxyState.getState() == AuthProtocolState.UNCHALLENGED) {
            final AuthScheme authScheme = authCache.get(proxy);
            if (authScheme != null) {
                doPreemptiveAuth(proxy, authScheme, proxyState, credsProvider);
            }
        }
    }

    private void doPreemptiveAuth(
            final HttpHost host,
            final AuthScheme authScheme,
            final AuthState authState,
            final CredentialsProvider credsProvider) {
        final String schemeName = authScheme.getSchemeName();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Re-using cached '" + schemeName + "' auth scheme for " + host);
        }

        final AuthScope authScope = new AuthScope(host, AuthScope.ANY_REALM, schemeName);
        final Credentials creds = credsProvider.getCredentials(authScope);

        if (creds != null) {
            authState.update(authScheme, creds);
        } else {
            this.log.debug("No credentials for preemptive authentication");
        }
    }

}
