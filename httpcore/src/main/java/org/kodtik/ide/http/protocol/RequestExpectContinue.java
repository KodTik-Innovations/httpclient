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

package org.kodtik.ide.http.protocol;

import java.io.IOException;

import org.kodtik.ide.http.HttpEntity;
import org.kodtik.ide.http.HttpEntityEnclosingRequest;
import org.kodtik.ide.http.HttpException;
import org.kodtik.ide.http.HttpRequest;
import org.kodtik.ide.http.HttpRequestInterceptor;
import org.kodtik.ide.http.HttpVersion;
import org.kodtik.ide.http.ProtocolVersion;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.params.CoreProtocolPNames;
import org.kodtik.ide.http.util.Args;

/**
 * RequestExpectContinue is responsible for enabling the 'expect-continue'
 * handshake by adding {@code Expect} header. This interceptor is
 * recommended for client side protocol processors.
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
@SuppressWarnings("deprecation")
public class RequestExpectContinue implements HttpRequestInterceptor {

    private final boolean activeByDefault;

    /**
     * @deprecated (4.3) use {@link org.kodtik.ide.http.protocol.RequestExpectContinue#RequestExpectContinue(boolean)}
     */
    @Deprecated
    public RequestExpectContinue() {
        this(false);
    }

    /**
     * @since 4.3
     */
    public RequestExpectContinue(final boolean activeByDefault) {
        super();
        this.activeByDefault = activeByDefault;
    }

    @Override
    public void process(final HttpRequest request, final HttpContext context)
            throws HttpException, IOException {
        Args.notNull(request, "HTTP request");

        if (!request.containsHeader(HTTP.EXPECT_DIRECTIVE)) {
            if (request instanceof HttpEntityEnclosingRequest) {
                final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
                final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
                // Do not send the expect header if request body is known to be empty
                if (entity != null
                        && entity.getContentLength() != 0 && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                    final boolean active = request.getParams().getBooleanParameter(
                            CoreProtocolPNames.USE_EXPECT_CONTINUE, this.activeByDefault);
                    if (active) {
                        request.addHeader(HTTP.EXPECT_DIRECTIVE, HTTP.EXPECT_CONTINUE);
                    }
                }
            }
        }
    }

}
