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

import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.cookie.ClientCookie;
import org.kodtik.ide.http.cookie.CommonCookieAttributeHandler;
import org.kodtik.ide.http.cookie.Cookie;
import org.kodtik.ide.http.cookie.CookieOrigin;
import org.kodtik.ide.http.cookie.MalformedCookieException;
import org.kodtik.ide.http.cookie.SetCookie;
import org.kodtik.ide.http.util.Args;
import org.kodtik.ide.http.util.TextUtils;

/**
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicPathHandler implements CommonCookieAttributeHandler {

    public BasicPathHandler() {
        super();
    }

    @Override
    public void parse(
            final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        cookie.setPath(!TextUtils.isBlank(value) ? value : "/");
    }

    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin)
            throws MalformedCookieException {
    }

    static boolean pathMatch(final String uriPath, final String cookiePath) {
        String normalizedCookiePath = cookiePath;
        if (normalizedCookiePath == null) {
            normalizedCookiePath = "/";
        }
        if (normalizedCookiePath.length() > 1 && normalizedCookiePath.endsWith("/")) {
            normalizedCookiePath = normalizedCookiePath.substring(0, normalizedCookiePath.length() - 1);
        }
        if (uriPath.startsWith(normalizedCookiePath)) {
            if (normalizedCookiePath.equals("/")) {
                return true;
            }
            if (uriPath.length() == normalizedCookiePath.length()) {
                return true;
            }
            if (uriPath.charAt(normalizedCookiePath.length()) == '/') {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        return pathMatch(origin.getPath(), cookie.getPath());
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.PATH_ATTR;
    }

}
