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

import java.io.IOException;

import org.kodtik.ide.http.HttpEntity;
import org.kodtik.ide.http.HttpResponse;
import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.client.HttpResponseException;
import org.kodtik.ide.http.util.EntityUtils;

/**
 * A {@link org.kodtik.ide.http.client.ResponseHandler} that returns the response body as a String
 * for successful (2xx) responses. If the response code was &gt;= 300, the response
 * body is consumed and an {@link org.kodtik.ide.http.client.HttpResponseException} is thrown.
 * <p>
 * If this is used with
 * {@link org.kodtik.ide.http.client.HttpClient#execute(
 *  org.kodtik.ide.http.client.methods.HttpUriRequest, org.kodtik.ide.http.client.ResponseHandler)},
 * HttpClient may handle redirects (3xx responses) internally.
 * </p>
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicResponseHandler extends AbstractResponseHandler<String> {

    /**
     * Returns the entity as a body as a String.
     */
    @Override
    public String handleEntity(final HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity);
    }

    @Override
    public String handleResponse(
            final HttpResponse response) throws HttpResponseException, IOException {
        return super.handleResponse(response);
    }

}
