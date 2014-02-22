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

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.osgl.cache.CacheService;

/**
 * Created by luog on 17/02/14.
 */
public enum EhCacheService implements CacheService {

    INSTANCE;

    CacheManager cacheManager;

    net.sf.ehcache.Cache cache;

    private static final String cacheName = "osgl-cache";

    private int defaultTTL = 60;

    private EhCacheService() {
        this.cacheManager = CacheManager.create();
        this.cacheManager.addCache(cacheName);
        this.cache = cacheManager.getCache(cacheName);
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
    }

    @Override
    public void startup() {
    }
}
