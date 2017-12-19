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

import org.osgl.cache.CacheService;
import org.osgl.cache.CacheServiceProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by luog on 18/02/14.
 */
public class MemcachedServiceProvider implements CacheServiceProvider {

    public static MemcachedServiceProvider INSTANCE = new MemcachedServiceProvider();

    private static ConcurrentMap<String, CacheService> services = new ConcurrentHashMap<String, CacheService>();

    @Override
    public CacheService get() {
        return MemcachedService.getInstance();
    }

    @Override
    public CacheService get(String name) {
        return MemcachedService.getInstance();
    }
}
