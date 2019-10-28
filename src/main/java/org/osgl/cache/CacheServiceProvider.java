/*
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.osgl.cache;

/*-
 * #%L
 * OSGL Cache API
 * %%
 * Copyright (C) 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import net.sf.ehcache.Ehcache;
import org.osgl.$;
import org.osgl.cache.impl.NullCacheService;
import org.osgl.cache.impl.SimpleCacheService;
import org.osgl.cache.impl.SimpleCacheServiceProvider;
import org.osgl.util.S;
import osgl.version.Version;
import osgl.version.Versioned;

/**
 * The factory to get CacheService instance
 */
@Versioned
public interface CacheServiceProvider {

    Version VERSION = Version.get();

    /**
     * Get default cache service instance. The the default cache service
     * is not there yet, then create the default cache service instance
     * @return the default cache service instance
     */
    CacheService get();

    /**
     * Get cache service instance by name. If the service with the name is
     * not there yet, then create the instance
     *
     * @param name the name of the cache service
     * @return the cache service by name
     */
    CacheService get(String name);

    void reset();

    enum Impl implements CacheServiceProvider {
        NoCache() {
            @Override
            public CacheService get() {
                return NullCacheService.INSTANCE;
            }

            @Override
            public CacheService get(String name) {
                return get();
            }

            @Override
            public void reset() {
            }
        },
        Simple() {
            @Override
            public CacheService get() {
                return SimpleCacheServiceProvider.INSTANCE.get();
            }

            @Override
            public CacheService get(String name) {
                return SimpleCacheServiceProvider.INSTANCE.get(name);
            }

            @Override
            public void reset() {
                SimpleCacheServiceProvider.INSTANCE.reset();
            }
        },

        EhCache() {
            @Override
            public CacheService get() {
                CacheServiceProvider fact = $.newInstance("org.osgl.cache.impl.EhCacheServiceProvider");
                return fact.get();
            }

            @Override
            public CacheService get(String name) {
                CacheServiceProvider fact = $.newInstance("org.osgl.cache.impl.EhCacheServiceProvider");
                return fact.get(name);
            }

            @Override
            public void reset() {
                Class type = $.classForName("org.osgl.cache.impl.EhCacheServiceProvider");
                $.invokeStatic(type, "_reset");
            }
        },

        Memcached() {
            @Override
            public CacheService get() {
                CacheServiceProvider fact = $.newInstance("org.osgl.cache.impl.MemcachedServiceProvider");
                return fact.get();
            }

            @Override
            public CacheService get(String name) {
                CacheServiceProvider fact = $.newInstance("org.osgl.cache.impl.MemcachedServiceProvider");
                return fact.get(name);
            }

            @Override
            public void reset() {
                Class type = $.classForName("org.osgl.cache.impl.MemcachedServiceProvider");
                $.invokeStatic(type, "_reset");
            }
        },

        Auto() {
            private CacheServiceProvider realProvider;
            private CacheServiceProvider configured(String name) {
                if (S.blank(name)) {
                    name = "osgl.cache.impl";
                } else {
                    name = name.toLowerCase().trim();
                    if (!name.startsWith("osgl.cache.impl.")) {
                        name = "osgl.cache.impl." + name;
                    }
                }

                String cacheImpl = System.getProperty(name);
                if (S.notBlank(cacheImpl)) {
                    try {
                        return $.newInstance(cacheImpl);
                    } catch (Exception e) {
                        try {
                            CacheServiceProvider csp = Impl.valueOfIgnoreCase(cacheImpl);
                            return (csp == Auto) ? null : csp;
                        } catch (Exception e2) {
                            return null;
                        }
                    }
                }
                return null;
            }
            private CacheServiceProvider configured() {
                return configured(null);
            }
            @Override
            public CacheService get() {
                realProvider = configured();
                if (null != realProvider) {
                    return realProvider.get();
                }
                try {
                    realProvider = Memcached;
                    return realProvider.get();
                } catch (Throwable e) {
                    realProvider = null;
                }
                try {
                    realProvider = EhCache;
                    return realProvider.get();
                } catch (Throwable throwable) {
                    realProvider = null;
                }
                realProvider = Simple;
                return realProvider.get();
            }

            @Override
            public CacheService get(String name) {
                if (null == realProvider) {
                    get();
                }
                return realProvider.get(name);
            }

            @Override
            public void reset() {
                if (null != realProvider) {
                    realProvider.reset();
                }
            }
        };

        private static volatile ClassLoader classLoader;

        public static ClassLoader classLoader() {
            if (null == classLoader) {
                synchronized (CacheServiceProvider.class) {
                    if (null == classLoader) {
                        classLoader = Thread.currentThread().getContextClassLoader();
                    }
                }
            }
            return classLoader;
        }

        public static void setClassLoader(ClassLoader classLoader) {
            Impl.classLoader = $.notNull(classLoader);
        }

        public static CacheServiceProvider valueOfIgnoreCase(String name) {
            if (S.blank(name)) {
                return null;
            }
            name = name.trim().toLowerCase().intern();
            if (name == NoCache.name().intern()) {
                return NoCache;
            } else if (name == Simple.name().toLowerCase().intern()) {
                return Simple;
            } else if (name == Memcached.name().toLowerCase().intern()) {
                return Memcached;
            } else if (name == EhCache.name().toLowerCase().intern()) {
                return EhCache;
            } else if (name == Auto.name().toLowerCase().intern()) {
                return Auto;
            }
            return null;
        }
    }

}
