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

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.transcoders.SerializingTranscoder;
import org.osgl.cache.CacheServiceProvider;
import org.osgl.exception.ConfigurationException;
import org.osgl.logging.L;
import org.osgl.logging.Logger;
import org.osgl.util.E;
import org.osgl.util.S;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * The code of this implementation comes from Play!Framework 1.x
 */
public class MemcachedService extends CacheServiceBase {

    private static Logger logger = L.get(MemcachedService.class);

    private static MemcachedService instance;

    private MemcachedClient client;

    private SerializingTranscoder tc;

    private int defaultTTL = 60;

    public static MemcachedService getInstance() {
        return getInstance(false);
    }

    public static MemcachedService getInstance(boolean forceClientInit) {
        try {
            if (null == instance) {
                instance = new MemcachedService();
            } else if (forceClientInit) {
                // When you stop the client, it sets the interrupted state of this thread to true. If you try to reinit it with the same thread in this state,
                // Memcached client errors out. So a simple call to interrupted() will reset this flag
                Thread.interrupted();
                instance.initClient();
            }
        } catch (IOException e) {
            throw E.ioException(e);
        }
        return instance;
    }

    private MemcachedService() throws IOException {
        tc = new SerializingTranscoder() {
            @Override
            protected Object deserialize(byte[] data) {
                try {
                    return new ObjectInputStream(new ByteArrayInputStream(data)) {
                        @Override
                        protected Class<?> resolveClass(ObjectStreamClass desc)
                                throws IOException, ClassNotFoundException {
                            return Class.forName(desc.getName(), false, CacheServiceProvider.Impl.classLoader());
                        }
                    }.readObject();
                } catch (Exception e) {
                    logger.error(e, "Could not deserialize");
                }
                return null;
            }

            @Override
            protected byte[] serialize(Object object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    new ObjectOutputStream(bos).writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    logger.error(e, "Could not serialize");
                }
                return null;
            }
        };
        initClient();
    }


    public void initClient() throws IOException {
        System.setProperty("net.spy.log.LoggerImpl", "org.osgl.cache.impl.SpyMemcachedLogger");

        List<InetSocketAddress> addrs = MemcachedClientConfigurator.getHosts();
        if (addrs.isEmpty()) {
            throw new ConfigurationException("Bad configuration for memcached: missing host(s)");
        }
        String username = MemcachedClientConfigurator.getUsername();
        if (S.notBlank(username)) {
            String password = MemcachedClientConfigurator.getPassword();
            if (null == password) {
                throw new ConfigurationException("Bad configuration for memcached: missing password");
            }

            // Use plain SASL to connect to memcached
            AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"},
                    new PlainCallbackHandler(username, password));
            ConnectionFactory cf = new ConnectionFactoryBuilder()
                    .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                    .setAuthDescriptor(ad)
                    .build();
            client = new MemcachedClient(cf, addrs);
        } else {
            client = new MemcachedClient(addrs);
        }
    }

    @Override
    public void put(String key, Object value, int ttl) {
        client.set(key, ttl, value, tc);
    }

    @Override
    public void put(String key, Object value) {
        client.set(key, defaultTTL, value);
    }

    @Override
    public void evict(String key) {
        client.delete(key);
    }

    @Override
    public <T> T get(String key) {
        Future<Object> future = client.asyncGet(key, tc);
        try {
            return (T) future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(false);
        }
        return null;
    }

    @Override
    public void clear() {
        client.flush();
    }

    @Override
    public void setDefaultTTL(int ttl) {
        if (ttl <= 0) throw new IllegalArgumentException("time to live value couldn't be zero or negative number");
        defaultTTL = ttl;
    }

    @Override
    public synchronized void shutdown() {
        if (null != client) {
            client.shutdown();
            client = null;
        }
    }

    @Override
    public synchronized void startup() {
        if (null == client) {
            try {
                initClient();
            } catch (IOException e) {
                throw E.ioException(e);
            }
        }
    }
}
