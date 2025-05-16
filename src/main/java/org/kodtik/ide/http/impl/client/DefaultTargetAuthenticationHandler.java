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

import java.util.List;
import java.util.Map;

import org.kodtik.ide.http.Header;
import org.kodtik.ide.http.HttpResponse;
import org.kodtik.ide.http.HttpStatus;
import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.auth.AUTH;
import org.kodtik.ide.http.auth.MalformedChallengeException;
import org.kodtik.ide.http.auth.params.AuthPNames;
import org.kodtik.ide.http.protocol.HttpContext;
import org.kodtik.ide.http.util.Args;

/**
 * Default {@link org.kodtik.ide.http.client.AuthenticationHandler} implementation
 * for target host authentication.
 *
 * @since 4.0
 *
 * @deprecated (4.2)  use {@link TargetAuthenticationStrategy}
 */
@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultTargetAuthenticationHandler extends AbstractAuthenticationHandler {

    public DefaultTargetAuthenticationHandler() {
        super();
    }

    @Override
    public boolean isAuthenticationRequested(
            final HttpResponse response,
            final HttpContext context) {
        Args.notNull(response, "HTTP response");
        final int status = response.getStatusLine().getStatusCode();
        return status == HttpStatus.SC_UNAUTHORIZED;
    }

    @Override
    public Map<String, Header> getChallenges(
            final HttpResponse response,
            final HttpContext context) throws MalformedChallengeException {
        Args.notNull(response, "HTTP response");
        final Header[] headers = response.getHeaders(AUTH.WWW_AUTH);
        return parseChallenges(headers);
    }

    @Override
    protected List<String> getAuthPreferences(
            final HttpResponse response,
            final HttpContext context) {
        @SuppressWarnings("unchecked")
        final
        List<String> authpref = (List<String>) response.getParams().getParameter(
                AuthPNames.TARGET_AUTH_PREF);
        if (authpref != null) {
            return authpref;
        } else {
            return super.getAuthPreferences(response, context);
        }
    }

}

