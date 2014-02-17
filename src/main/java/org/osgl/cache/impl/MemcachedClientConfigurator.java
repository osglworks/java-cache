package org.osgl.cache.impl;

import net.spy.memcached.AddrUtil;
import org.osgl.util.S;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by luog on 17/02/14.
 */
public class MemcachedClientConfigurator {

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
            prop.load(MemcachedClientConfigurator.class.getResourceAsStream(CONF_FILE));
        } catch (IOException e) {
            // ignore
        }

        // try load System properties
        prop.putAll(System.getProperties());

        Config conf = new Config();
        if (prop.contains("memcached.host")) {
            conf.hosts = AddrUtil.getAddresses(prop.getProperty("memcached.host"));
        } else {
            int n = 1;
            String addresses = "";
            while (prop.containsKey("memcached." + n + ".host")) {
                addresses += prop.getProperty("memcached." + n + ".host") + " ";
            }
            if (S.notEmpty(addresses)) {
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
