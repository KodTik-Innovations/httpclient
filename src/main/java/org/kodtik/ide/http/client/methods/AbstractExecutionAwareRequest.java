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
package org.kodtik.ide.http.client.methods;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicMarkableReference;

import org.kodtik.ide.http.HttpRequest;
import org.kodtik.ide.http.client.utils.CloneUtils;
import org.kodtik.ide.http.concurrent.Cancellable;
import org.kodtik.ide.http.conn.ClientConnectionRequest;
import org.kodtik.ide.http.conn.ConnectionReleaseTrigger;
import org.kodtik.ide.http.message.AbstractHttpMessage;

@SuppressWarnings("deprecation")
public abstract class AbstractExecutionAwareRequest extends AbstractHttpMessage implements
        HttpExecutionAware, AbortableHttpRequest, Cloneable, HttpRequest {

    private final AtomicMarkableReference<Cancellable> cancellableRef;

    protected AbstractExecutionAwareRequest() {
        super();
        this.cancellableRef = new AtomicMarkableReference<Cancellable>(null, false);
    }

    /**
     * @deprecated Use {@link #setCancellable(Cancellable)}
     */
    @Override
    @Deprecated
    public void setConnectionRequest(final ClientConnectionRequest connRequest) {
        setCancellable(new Cancellable() {

            @Override
            public boolean cancel() {
                connRequest.abortRequest();
                return true;
            }

        });
    }

    /**
     * @deprecated Use {@link #setCancellable(Cancellable)}
     */
    @Override
    @Deprecated
    public void setReleaseTrigger(final ConnectionReleaseTrigger releaseTrigger) {
        setCancellable(new Cancellable() {

            @Override
            public boolean cancel() {
                try {
                    releaseTrigger.abortConnection();
                    return true;
                } catch (final IOException ex) {
                    return false;
                }
            }

        });
    }

    @Override
    public void abort() {
        while (!cancellableRef.isMarked()) {
            final Cancellable actualCancellable = cancellableRef.getReference();
            if (cancellableRef.compareAndSet(actualCancellable, actualCancellable, false, true)) {
                if (actualCancellable != null) {
                    actualCancellable.cancel();
                }
            }
        }
    }

    @Override
    public boolean isAborted() {
        return this.cancellableRef.isMarked();
    }

    /**
     * @since 4.2
     */
    @Override
    public void setCancellable(final Cancellable cancellable) {
        final Cancellable actualCancellable = cancellableRef.getReference();
        if (!cancellableRef.compareAndSet(actualCancellable, cancellable, false, false)) {
            cancellable.cancel();
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final AbstractExecutionAwareRequest clone = (AbstractExecutionAwareRequest) super.clone();
        clone.headergroup = CloneUtils.cloneObject(this.headergroup);
        clone.params = CloneUtils.cloneObject(this.params);
        return clone;
    }

    /**
     * @since 4.2
     *
     * @deprecated Do not use.
     */
    @Deprecated
    public void completed() {
        this.cancellableRef.set(null, false);
    }

    /**
     * Resets internal state of the request making it reusable.
     *
     * @since 4.2
     */
    public void reset() {
        for (;;) {
            final boolean marked = cancellableRef.isMarked();
            final Cancellable actualCancellable = cancellableRef.getReference();
            if (actualCancellable != null) {
                actualCancellable.cancel();
            }
            if (cancellableRef.compareAndSet(actualCancellable, null, marked, false)) {
                break;
            }
        }
    }

}
