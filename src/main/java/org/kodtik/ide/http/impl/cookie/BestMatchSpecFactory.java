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

package org.kodtik.ide.http.impl.cookie;

import java.util.Collection;

import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.cookie.CookieSpec;
import org.kodtik.ide.http.cookie.CookieSpecFactory;
import org.kodtik.ide.http.cookie.CookieSpecProvider;
import org.kodtik.ide.http.cookie.params.CookieSpecPNames;
import org.kodtik.ide.http.params.HttpParams;
import org.kodtik.ide.http.protocol.HttpContext;

/**
 * {@link org.kodtik.ide.http.cookie.CookieSpecProvider} implementation that provides an instance of
 * {@link org.kodtik.ide.http.impl.cookie.BestMatchSpec}. The instance returned by this factory can
 * be shared by multiple threads.
 *
 * @deprecated (4.4) use {@link org.kodtik.ide.http.impl.cookie.DefaultCookieSpecProvider}.
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public class BestMatchSpecFactory implements CookieSpecFactory, CookieSpecProvider {

    private final CookieSpec cookieSpec;

    public BestMatchSpecFactory(final String[] datepatterns, final boolean oneHeader) {
        super();
        this.cookieSpec = new BestMatchSpec(datepatterns, oneHeader);
    }

    public BestMatchSpecFactory() {
        this(null, false);
    }

    @Override
    public CookieSpec newInstance(final HttpParams params) {
        if (params != null) {

            String[] patterns = null;
            final Collection<?> param = (Collection<?>) params.getParameter(
                    CookieSpecPNames.DATE_PATTERNS);
            if (param != null) {
                patterns = new String[param.size()];
                patterns = param.toArray(patterns);
            }
            final boolean singleHeader = params.getBooleanParameter(
                    CookieSpecPNames.SINGLE_COOKIE_HEADER, false);

            return new BestMatchSpec(patterns, singleHeader);
        } else {
            return new BestMatchSpec();
        }
    }

    @Override
    public CookieSpec create(final HttpContext context) {
        return this.cookieSpec;
    }

}
