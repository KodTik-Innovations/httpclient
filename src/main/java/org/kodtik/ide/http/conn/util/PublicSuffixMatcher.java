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

import java.net.IDN;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kodtik.ide.http.annotation.Contract;
import org.kodtik.ide.http.annotation.ThreadingBehavior;
import org.kodtik.ide.http.util.Args;

/**
 * Utility class that can test if DNS names match the content of the Public Suffix List.
 * <p>
 * An up-to-date list of suffixes can be obtained from
 * <a href="http://publicsuffix.org/">publicsuffix.org</a>
 *
 * @see org.kodtik.ide.http.conn.util.PublicSuffixList
 *
 * @since 4.4
 */
@Contract(threading = ThreadingBehavior.SAFE)
public final class PublicSuffixMatcher {

    private final Map<String, DomainType> rules;
    private final Map<String, DomainType> exceptions;

    public PublicSuffixMatcher(final Collection<String> rules, final Collection<String> exceptions) {
        this(DomainType.UNKNOWN, rules, exceptions);
    }

    /**
     * @since 4.5
     */
    public PublicSuffixMatcher(
            final DomainType domainType, final Collection<String> rules, final Collection<String> exceptions) {
        Args.notNull(domainType,  "Domain type");
        Args.notNull(rules,  "Domain suffix rules");
        this.rules = new ConcurrentHashMap<String, DomainType>(rules.size());
        for (final String rule: rules) {
            this.rules.put(rule, domainType);
        }
        this.exceptions = new ConcurrentHashMap<String, DomainType>();
        if (exceptions != null) {
            for (final String exception: exceptions) {
                this.exceptions.put(exception, domainType);
            }
        }
    }

    /**
     * @since 4.5
     */
    public PublicSuffixMatcher(final Collection<PublicSuffixList> lists) {
        Args.notNull(lists,  "Domain suffix lists");
        this.rules = new ConcurrentHashMap<String, DomainType>();
        this.exceptions = new ConcurrentHashMap<String, DomainType>();
        for (final PublicSuffixList list: lists) {
            final DomainType domainType = list.getType();
            final List<String> rules = list.getRules();
            for (final String rule: rules) {
                this.rules.put(rule, domainType);
            }
            final List<String> exceptions = list.getExceptions();
            if (exceptions != null) {
                for (final String exception: exceptions) {
                    this.exceptions.put(exception, domainType);
                }
            }
        }
    }

    private static DomainType findEntry(final Map<String, DomainType> map, final String rule) {
        if (map == null) {
            return null;
        }
        return map.get(rule);
    }

    private static boolean match(final DomainType domainType, final DomainType expectedType) {
        return domainType != null && (expectedType == null || domainType.equals(expectedType));
    }

    /**
     * Returns registrable part of the domain for the given domain name or {@code null}
     * if given domain represents a public suffix.
     *
     * @param domain
     * @return domain root
     */
    public String getDomainRoot(final String domain) {
        return getDomainRoot(domain, null);
    }

    /**
     * Returns registrable part of the domain for the given domain name or {@code null}
     * if given domain represents a public suffix.
     *
     * @param domain
     * @param expectedType expected domain type or {@code null} if any.
     * @return domain root
     *
     * @since 4.5
     */
    public String getDomainRoot(final String domain, final DomainType expectedType) {
        if (domain == null) {
            return null;
        }
        if (domain.startsWith(".")) {
            return null;
        }
        final String normalized = DnsUtils.normalize(domain);
        String segment = normalized;
        String result = null;
        while (segment != null) {
            // An exception rule takes priority over any other matching rule.
            final String key = IDN.toUnicode(segment);
            final DomainType exceptionRule = findEntry(exceptions, key);
            if (match(exceptionRule, expectedType)) {
                return segment;
            }
            final DomainType domainRule = findEntry(rules, key);
            if (match(domainRule, expectedType)) {
                if (domainRule == DomainType.PRIVATE) {
                    return segment;
                }
                return result;
            }

            final int nextdot = segment.indexOf('.');
            final String nextSegment = nextdot != -1 ? segment.substring(nextdot + 1) : null;

            if (nextSegment != null) {
                final DomainType wildcardDomainRule = findEntry(rules, "*." + IDN.toUnicode(nextSegment));
                if (match(wildcardDomainRule, expectedType)) {
                    if (wildcardDomainRule == DomainType.PRIVATE) {
                        return segment;
                    }
                    return result;
                }
            }
            result = segment;
            segment = nextSegment;
        }

        // If no expectations then this result is good.
        if (expectedType == null || expectedType == DomainType.UNKNOWN) {
            return result;
        }

        // If we did have expectations apparently there was no match
        return null;
    }

    /**
     * Tests whether the given domain matches any of entry from the public suffix list.
     */
    public boolean matches(final String domain) {
        return matches(domain, null);
    }

    /**
     * Tests whether the given domain matches any of entry from the public suffix list.
     *
     * @param domain
     * @param expectedType expected domain type or {@code null} if any.
     * @return {@code true} if the given domain matches any of the public suffixes.
     *
     * @since 4.5
     */
    public boolean matches(final String domain, final DomainType expectedType) {
        if (domain == null) {
            return false;
        }
        final String domainRoot = getDomainRoot(
                domain.startsWith(".") ? domain.substring(1) : domain, expectedType);
        return domainRoot == null;
    }

}
