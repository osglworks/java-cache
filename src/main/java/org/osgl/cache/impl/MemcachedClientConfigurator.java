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

import net.spy.memcached.AddrUtil;
import org.osgl.logging.L;
import org.osgl.logging.Logger;
import org.osgl.util.S;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by luog on 17/02/14.
 */
public class MemcachedClientConfigurator {

    protected static final Logger logger = L.get(MemcachedClientConfigurator.class);

    public static final String CONF_FILE = "memcached.properties";

    private static class Config {
        private List<InetSocketAddress> hosts = new ArrayList<InetSocketAddress>();
        private String username;
        private String password;
    }

    private static volatile Config conf = null;

    private static void autoConfig() {
        // try load properties
        Properties prop = new Properties();
        try {
            URL url = null;
            url = Thread.currentThread().getContextClassLoader().getResource(CONF_FILE);
            if (null == url) {
                url = MemcachedClientConfigurator.class.getResource(CONF_FILE);
            }
            if (null == url) {
                logger.warn("Cannot find " + CONF_FILE + " in class path");
            } else {
                prop.load(url.openStream());
            }
        } catch (IOException e) {
            logger.error(e, "error loading memcached client configuration file: " + CONF_FILE);
        }

        // try load System properties
        prop.putAll(System.getProperties());

        Config conf = new Config();
        if (prop.containsKey("memcached.host")) {
            String s = prop.getProperty("memcached.host");
            if (!s.contains(":")) {
                s = s + ":11211";
            }
            conf.hosts = AddrUtil.getAddresses(s);
        } else {
            int n = 1;
            String addresses = "";
            while (prop.containsKey("memcached." + n + ".host")) {
                String s = prop.getProperty("memcached." + n + ".host");
                if (!s.contains(":")) {
                    s = s + ":11211";
                }
                addresses += s + " ";
            }
            if (S.notBlank(addresses)) {
                conf.hosts = AddrUtil.getAddresses(addresses);
            }
        }

        if (prop.containsKey("memcached.user")) {
            conf.username = prop.getProperty("memcached.user");
        } else if (prop.containsKey("memcached.username")) {
            conf.username = prop.getProperty("memcached.username");
        }
        conf.password = prop.getProperty("memcached.password");

        MemcachedClientConfigurator.conf = conf;
    }

    private static Config conf() {
        if (null == conf) {
            synchronized (MemcachedClientConfigurator.class) {
                if (null == conf) autoConfig();
            }
        }
        return conf;
    }

    public static void setHosts(String addr1, String... addrs) {
        String addr = S.join(" ", addr1, S.join(" ", addrs));
        conf().hosts = AddrUtil.getAddresses(addr);
    }

    public static void setUsernamePassword(String username, String password) {
        conf().username = username;
        conf().password = password;
    }

    public static List<InetSocketAddress> getHosts() {
        return conf().hosts;
    }

    public static String getUsername() {
        return conf().username;
    }

    public static String getPassword() {
        return conf().password;
    }

    public static void applyConfig() throws IOException {
        MemcachedService.getInstance(true);
    }
}
