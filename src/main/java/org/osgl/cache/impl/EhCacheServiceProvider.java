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

import org.osgl.cache.CacheService;
import org.osgl.cache.CacheServiceProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Returns instance of {@link org.osgl.cache.impl.EhCacheService}
 */
public class EhCacheServiceProvider implements CacheServiceProvider {
    public static EhCacheServiceProvider INSTANCE = new EhCacheServiceProvider();
    private static ConcurrentMap<String, CacheService> services = new ConcurrentHashMap<String, CacheService>();

    @Override
    public CacheService get() {
        return get(CacheService.DEF_CACHE_NAME);
    }

    @Override
    public CacheService get(String name) {
        CacheService cs = services.get(name);
        if (null == cs) {
            cs = new EhCacheService(name);
            services.putIfAbsent(name, cs);
        }
        return cs;
    }
}
