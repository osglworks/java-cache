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

import org.osgl._;
import org.osgl.cache.impl.NullCacheService;
import org.osgl.cache.impl.SimpleCacheService;

/**
 * Created by luog on 17/02/14.
 */
public interface CacheServiceFactory {
    CacheService get();

    public static enum Impl implements CacheServiceFactory {
        NoCache() {
            @Override
            public CacheService get() {
                return NullCacheService.INSTANCE;
            }
        },
        Simple() {
            @Override
            public CacheService get() {
                return SimpleCacheService.INSTANCE;
            }
        },
        EhCache() {
            @Override
            public CacheService get() {
                CacheServiceFactory fact = _.newInstance("org.osgl.cache.impl.EhCacheServiceFactory");
                return fact.get();
            }
        },
        Memcached() {
            @Override
            public CacheService get() {
                CacheServiceFactory fact = _.newInstance("org.osgl.cache.impl.MemcachedServiceFactory");
                return fact.get();
            }
        },
        Auto() {
            @Override
            public CacheService get() {
                try {
                    return Memcached.get();
                } catch (Throwable e) {
                    // ignore
                }
                try {
                    return EhCache.get();
                } catch (Throwable throwable) {
                    // ignore
                }
                return Simple.get();
            }
        }
    }
}
