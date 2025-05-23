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

package org.kodtik.ide.http.conn.util;

/**
 * A collection of utilities relating to Domain Name System.
 *
 * @since 4.5
 */
public class DnsUtils {

    private DnsUtils() {
    }

    private static boolean isUpper(final char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static String normalize(final String s) {
        if (s == null) {
            return null;
        }
        int pos = 0;
        int remaining = s.length();
        while (remaining > 0) {
            if (isUpper(s.charAt(pos))) {
                break;
            }
            pos++;
            remaining--;
        }
        if (remaining > 0) {
            final StringBuilder buf = new StringBuilder(s.length());
            buf.append(s, 0, pos);
            while (remaining > 0) {
                final char c = s.charAt(pos);
                if (isUpper(c)) {
                    buf.append((char) (c + ('a' - 'A')));
                } else {
                    buf.append(c);
                }
                pos++;
                remaining--;
            }
            return buf.toString();
        } else {
            return s;
        }
    }

}
