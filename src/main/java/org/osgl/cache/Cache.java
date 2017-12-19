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

/**
 * The Cache service static facade which delegate the operations
 * to the default cache service created by auto discovered cache
 * service factory
 */
public enum Cache {
    ;

    private static volatile CacheService c_;
    private static CacheService c() {
        if (null == c_) {
            synchronized (Cache.class) {
                if (null == c_) {
                    CacheServiceProvider fact = CacheServiceProvider.Impl.Auto;
                    c_ = fact.get();
                }
            }
        }
        return c_;
    }

    public static void startup() {
        c().startup();
    }

    public static void shutdown() {
        c().shutdown();
    }

    public static void put(String key, Object value, int ttl) {
        c().put(key, value, ttl);
    }

    public static void put(String key, Object value) {
        c().put(key, value);
    }

    public static <T> T get(String key) {
        return c().get(key);
    }

    public static void evict(String key) {
        c().evict(key);
    }


}
