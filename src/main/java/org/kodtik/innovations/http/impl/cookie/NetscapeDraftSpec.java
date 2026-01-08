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

package org.kodtik.innovations.http.impl.cookie;

import java.util.ArrayList;
import java.util.List;

import org.kodtik.innovations.http.FormattedHeader;
import org.kodtik.innovations.http.Header;
import org.kodtik.innovations.http.HeaderElement;
import org.kodtik.innovations.http.annotation.Contract;
import org.kodtik.innovations.http.annotation.Obsolete;
import org.kodtik.innovations.http.annotation.ThreadingBehavior;
import org.kodtik.innovations.http.cookie.CommonCookieAttributeHandler;
import org.kodtik.innovations.http.cookie.Cookie;
import org.kodtik.innovations.http.cookie.CookieOrigin;
import org.kodtik.innovations.http.cookie.MalformedCookieException;
import org.kodtik.innovations.http.cookie.SM;
import org.kodtik.innovations.http.message.BufferedHeader;
import org.kodtik.innovations.http.message.ParserCursor;
import org.kodtik.innovations.http.util.Args;
import org.kodtik.innovations.http.util.CharArrayBuffer;

/**
 * This {@link org.kodtik.innovations.http.cookie.CookieSpec} implementation conforms to
 * the original draft specification published by Netscape Communications.
 * It should be avoided unless absolutely necessary for compatibility with
 * legacy applications.
 * <p>
 * Rendered obsolete by {@link org.kodtik.innovations.http.impl.cookie.RFC6265LaxSpec}.
 *
 * @since 4.0
 * @see org.kodtik.innovations.http.impl.cookie.RFC6265LaxSpec
 */
@Obsolete
@Contract(threading = ThreadingBehavior.SAFE)
public class NetscapeDraftSpec extends CookieSpecBase {

    protected static final String EXPIRES_PATTERN = "EEE, dd-MMM-yy HH:mm:ss z";

    /** Default constructor */
    public NetscapeDraftSpec(final String[] datepatterns) {
        super(new BasicPathHandler(),
                new NetscapeDomainHandler(),
                new BasicSecureHandler(),
                new BasicCommentHandler(),
                new BasicExpiresHandler(
                        datepatterns != null ? datepatterns.clone() : new String[]{EXPIRES_PATTERN}));
    }

    NetscapeDraftSpec(final CommonCookieAttributeHandler... handlers) {
        super(handlers);
    }

    public NetscapeDraftSpec() {
        this((String[]) null);
    }

    /**
      * Parses the Set-Cookie value into an array of {@code Cookie}s.
      *
      * <p>Syntax of the Set-Cookie HTTP Response Header:</p>
      *
      * <p>This is the format a CGI script would use to add to
      * the HTTP headers a new piece of data which is to be stored by
      * the client for later retrieval.</p>
      *
      * <PRE>
      *  Set-Cookie: NAME=VALUE; expires=DATE; path=PATH; domain=DOMAIN_NAME; secure
      * </PRE>
      *
      * <p>Please note that the Netscape draft specification does not fully conform to the HTTP
      * header format. Comma character if present in {@code Set-Cookie} will not be treated
      * as a header element separator</p>
      *
      * @see <a href="http://web.archive.org/web/20020803110822/http://wp.netscape.com/newsref/std/cookie_spec.html">
      *  The Cookie Spec.</a>
      *
      * @param header the {@code Set-Cookie} received from the server
      * @return an array of {@code Cookie}s parsed from the Set-Cookie value
      * @throws MalformedCookieException if an exception occurs during parsing
      */
    @Override
    public List<Cookie> parse(final Header header, final CookieOrigin origin)
            throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (!header.getName().equalsIgnoreCase(SM.SET_COOKIE)) {
            throw new MalformedCookieException("Unrecognized cookie header '"
                    + header.toString() + "'");
        }
        final NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
        final CharArrayBuffer buffer;
        final ParserCursor cursor;
        if (header instanceof FormattedHeader) {
            buffer = ((FormattedHeader) header).getBuffer();
            cursor = new ParserCursor(
                    ((FormattedHeader) header).getValuePos(),
                    buffer.length());
        } else {
            final String s = header.getValue();
            if (s == null) {
                throw new MalformedCookieException("Header value is null");
            }
            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            cursor = new ParserCursor(0, buffer.length());
        }
        return parse(new HeaderElement[] { parser.parseHeader(buffer, cursor) }, origin);
    }

    @Override
    public List<Header> formatCookies(final List<Cookie> cookies) {
        Args.notEmpty(cookies, "List of cookies");
        final CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
        buffer.append(SM.COOKIE);
        buffer.append(": ");
        for (int i = 0; i < cookies.size(); i++) {
            final Cookie cookie = cookies.get(i);
            if (i > 0) {
                buffer.append("; ");
            }
            buffer.append(cookie.getName());
            final String s = cookie.getValue();
            if (s != null) {
                buffer.append("=");
                buffer.append(s);
            }
        }
        final List<Header> headers = new ArrayList<Header>(1);
        headers.add(new BufferedHeader(buffer));
        return headers;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public Header getVersionHeader() {
        return null;
    }

    @Override
    public String toString() {
        return "netscape";
    }

}
