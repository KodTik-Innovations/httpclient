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
import org.kodtik.ide.http.cookie.MalformedCookieException;
import org.kodtik.ide.http.cookie.SetCookie;
import org.kodtik.ide.http.util.Args;

/**
 * {@code "Version"} cookie attribute handler for BrowserCompat cookie spec.
 *
 *  @deprecated (4.4) no longer used.
 *
 * @since 4.3
 */
@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BrowserCompatVersionAttributeHandler extends
        AbstractCookieAttributeHandler implements CommonCookieAttributeHandler {

    public BrowserCompatVersionAttributeHandler() {
        super();
    }

    /**
     * Parse cookie version attribute.
     */
    @Override
    public void parse(final SetCookie cookie, final String value)
            throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for version attribute");
        }
        int version = 0;
        try {
            version = Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            // Just ignore invalid versions
        }
        cookie.setVersion(version);
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.VERSION_ATTR;
    }

}
