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

import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.client.methods.HttpDelete;
import org.kodtik.ide.http.client.methods.HttpGet;
import org.kodtik.ide.http.client.methods.HttpHead;
import org.kodtik.ide.http.client.methods.HttpPost;

/**
 * Lax {@link org.kodtik.ide.http.client.RedirectStrategy} implementation
 * that automatically redirects all HEAD, GET, POST, and DELETE requests.
 * This strategy relaxes restrictions on automatic redirection of
 * POST methods imposed by the HTTP specification.
 *
 * @since 4.2
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxRedirectStrategy extends DefaultRedirectStrategy {

    public static final LaxRedirectStrategy INSTANCE = new LaxRedirectStrategy();

    public LaxRedirectStrategy() {
        super(new String[] {
            HttpGet.METHOD_NAME,
            HttpPost.METHOD_NAME,
            HttpHead.METHOD_NAME,
            HttpDelete.METHOD_NAME
        });
    }

}
