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

import java.util.Locale;

import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.conn.util.InetAddressUtils;
import org.kodtik.ide.http.cookie.ClientCookie;
import org.kodtik.ide.http.cookie.CommonCookieAttributeHandler;
import org.kodtik.ide.http.cookie.Cookie;
import org.kodtik.ide.http.cookie.CookieOrigin;
import org.kodtik.ide.http.cookie.CookieRestrictionViolationException;
import org.kodtik.ide.http.cookie.MalformedCookieException;
import org.kodtik.ide.http.cookie.SetCookie;
import org.kodtik.ide.http.util.Args;
import org.kodtik.ide.http.util.TextUtils;

/**
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicDomainHandler implements CommonCookieAttributeHandler {

    public BasicDomainHandler() {
        super();
    }

    @Override
    public void parse(final SetCookie cookie, final String value)
            throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (TextUtils.isBlank(value)) {
            throw new MalformedCookieException("Blank or null value for domain attribute");
        }
        // Ignore domain attributes ending with '.' per RFC 6265, 4.1.2.3
        if (value.endsWith(".")) {
            return;
        }
        String domain = value;
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        domain = domain.toLowerCase(Locale.ROOT);
        cookie.setDomain(domain);
    }

    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin)
            throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        // Validate the cookies domain attribute.  NOTE:  Domains without
        // any dots are allowed to support hosts on private LANs that don't
        // have DNS names.  Since they have no dots, to domain-match the
        // request-host and domain must be identical for the cookie to sent
        // back to the origin-server.
        final String host = origin.getHost();
        final String domain = cookie.getDomain();
        if (domain == null) {
            throw new CookieRestrictionViolationException("Cookie 'domain' may not be null");
        }
        if (!host.equals(domain) && !domainMatch(domain, host)) {
            throw new CookieRestrictionViolationException(
                    "Illegal 'domain' attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
        }
    }

    static boolean domainMatch(final String domain, final String host) {
        if (InetAddressUtils.isIPv4Address(host) || InetAddressUtils.isIPv6Address(host)) {
            return false;
        }
        final String normalizedDomain = domain.startsWith(".") ? domain.substring(1) : domain;
        if (host.endsWith(normalizedDomain)) {
            final int prefix = host.length() - normalizedDomain.length();
            // Either a full match or a prefix endidng with a '.'
            if (prefix == 0) {
                return true;
            }
            if (prefix > 1 && host.charAt(prefix - 1) == '.') {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        final String host = origin.getHost();
        String domain = cookie.getDomain();
        if (domain == null) {
            return false;
        }
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        domain = domain.toLowerCase(Locale.ROOT);
        if (host.equals(domain)) {
            return true;
        }
        if (cookie instanceof ClientCookie) {
            if (((ClientCookie) cookie).containsAttribute(ClientCookie.DOMAIN_ATTR)) {
                return domainMatch(domain, host);
            }
        }
        return false;
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.DOMAIN_ATTR;
    }

}
