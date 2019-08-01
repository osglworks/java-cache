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
package org.osgl.cache.impl;

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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import org.osgl.cache.CacheService;
import org.osgl.cache.CacheServiceProvider;
import org.osgl.util.S;

/**
 * Created by luog on 17/02/14.
 */
public class EhCacheService extends CacheServiceBase {

    private volatile static EhCacheService INSTANCE = null;

    CacheManager cacheManager;

    net.sf.ehcache.Cache cache;

    private String cacheName = DEF_CACHE_NAME;

    private int defaultTTL = 60;

    public EhCacheService(String name) {
        if (S.notBlank(name)) {
            cacheName = name;
        }
        Configuration configuration = ConfigurationFactory.parseConfiguration();
        configuration.setClassLoader(CacheServiceProvider.Impl.classLoader());
        cacheManager = CacheManager.create(configuration);
        Cache cache = cacheManager.getCache(cacheName);
        if (null == cache) {
            cache = (Cache)cacheManager.addCacheIfAbsent(cacheName);
        }
        this.cache = cache;
        long l = this.cache.getCacheConfiguration().getTimeToLiveSeconds();
        if (0 != l) {
            defaultTTL = (int)l;
        }
    }

    @Override
    public void put(String key, Object value, int ttl) {
        Element element = new Element(key, value);
        if (0 >= ttl) ttl = defaultTTL;
        element.setTimeToLive(ttl);
        cache.put(element);
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, defaultTTL);
    }

    @Override
    public void evict(String key) {
        cache.remove(key);
    }

    @Override
    public <T> T get(String key) {
        Element e = cache.get(key);
        if (null == e) {
            return null;
        }
        return (T) e.getObjectValue();
    }

    @Override
    public void clear() {
        cache.removeAll();
    }

    @Override
    public void setDefaultTTL(int ttl) {
        if (ttl <= 0) throw new IllegalArgumentException("time to live value couldn't be zero or negative number");
        defaultTTL = ttl;
    }

    @Override
    public void shutdown() {
        clear();
        cacheManager.shutdown();
    }

    @Override
    public void startup() {
    }

    static CacheService defaultInstance() {
        if (null == INSTANCE) {
            synchronized (CacheService.class) {
                if (null == INSTANCE) {
                    INSTANCE = new EhCacheService(DEF_CACHE_NAME);
                }
            }
        }
        return INSTANCE;
    }
}
