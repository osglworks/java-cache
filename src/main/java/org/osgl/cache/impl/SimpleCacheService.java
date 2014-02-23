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
import org.osgl.logging.L;
import org.osgl.logging.Logger;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple cache service implementation based on concurrent hash map
 */
public class SimpleCacheService implements CacheService {

    private static CacheService INSTANCE = null;

    private String name;

    private static final Logger logger = L.get(SimpleCacheService.class);

    private static class TimerThreadFactory implements ThreadFactory {
        private String name;
        TimerThreadFactory(String name) {
            this.name = name;
        }
        private static final AtomicInteger threadNumber = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            SecurityManager s = System.getSecurityManager();
            ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

            Thread t = new Thread(group, r, "simple-cache-service-" + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private ScheduledExecutorService scheduler = null;

    SimpleCacheService(String name) {
        this.name = name;
        startup();
    }

    private static class Item implements Comparable<Item> {
        String key;
        Object value;
        long ts;
        int ttl;

        Item(String key, Object value, int ttl) {
            this.key = key;
            this.value = value;
            this.ttl = ttl;
            this.ts = System.currentTimeMillis();
        }
        
        @Override
        public int compareTo(Item that) {
            return ttl - that.ttl;
        }
    }

    private ConcurrentHashMap<String, Item> cache_ = new ConcurrentHashMap<String, Item>();
    private Queue<Item> items_ = new PriorityQueue<Item>();

    @Override
    public void put(String key, Object value, int ttl) {
        if (null == key) throw new NullPointerException();
        if (0 >= ttl) {
            ttl = defaultTTL;
        }
        Item item = cache_.get(key);
        if (null == item) {
            Item newItem = new Item(key, value, ttl);
            item = cache_.putIfAbsent(key, newItem);
            if (null != item) {
                item.value = value;
                item.ttl = ttl;
            } else {
                items_.offer(newItem);
            }
        } else {
            item.value = value;
            item.ttl = ttl;
        }
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, defaultTTL);
    }

    @Override
    public void evict(String key) {
        cache_.remove(key);
    }

    @Override
    public void clear() {
        cache_.clear();
        items_.clear();
    }
    
    @Override
    public <T> T get(String key) {
        Item item = cache_.get(key);
        if (null == item) {
            return null;
        }
        return (T)item.value;
    }

    private int defaultTTL = 60;

    @Override
    public void setDefaultTTL(int ttl) {
        if (ttl == 0) throw new IllegalArgumentException("time to live value couldn't be zero");
        this.defaultTTL = ttl;
    }

    @Override
    public synchronized void shutdown() {
        clear();
        if (null != scheduler) {
            scheduler.shutdown();
            scheduler = null;
        }
    }
    
    @Override
    public synchronized void startup() {
        if (null == scheduler) {
            scheduler = new ScheduledThreadPoolExecutor(1, new TimerThreadFactory(name));
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (items_.size() == 0) {
                        return;
                    }
                    long now = System.currentTimeMillis();
                    logger.trace(">>>>now:%s", now);
                    while(true) {
                        Item item = items_.peek();
                        if (null == item) {
                            break;
                        }
                        long ts = item.ts + item.ttl * 1000;
                        if ((ts) < now + 50) {
                            items_.poll();
                            cache_.remove(item.key);
                            logger.trace("- %s at %s", item.key, ts);
                            continue;
                        } else {
                            logger.trace(">>>>ts:  %s", ts);
                        }
                        break;
                    }
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    shutdown();
                }
            });
        }
    }

}
