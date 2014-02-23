java-cache
==========

A simple cache api supports the following cache services

* EhCache
* Memcached
* Built-in ConcurrentMap based cache
* Built-in NoCache service

### Usage

1. Get a CacheService instance

```java
// Get EhCache service
CacheService cache = CacheServiceProvider.Impl.EhCache.get();
// Get Memcached service
CacheService cache = CacheServiceProvider.Impl.Memcached.get();
// Get Simple In Memory cache service
CacheService cache = CacheServiceProvider.Impl.Simple.get();
// Get No Cache (Null) Service
CacheService cache = CacheServiceProvider.Impl.NoCache.get();
// Automatically detect cache service provider
CacheService cache = CacheServiceProvider.Impl.Auto.get();

// Get a cache service with name specified
CacheService cache = CacheServiceProvider.Impl.XX.get(myCacheName);
```

Note, Memcached service always returns the same `CacheService` instance even if a name is specified

When `CacheServiceProvider.Impl.Auto` is used to get the `CacheService`, it will first try to load Memcached cache service, if failed, then try the EhCache service; if it still failed, then load the in memory Simple cache service.

2. Use Cache service

```java
// Add an object to cache
cache.put(key, object, ttl); // ttl is the int value specify the object's time to live in seconds
// Add an object to cache with default ttl
cache.put(key, object);
// Get an object from cache by key
Object obj = cache.get(key);
// evict a cache entry by key
cache.evict(key);
```

3. Start/Stop a cache service

```java
cache.startup();
cache.shutdown();
```

Note, not these two methods are only used with the Simple cache service, which start up/shutdown a timer thread. Memcached and EhCache service does not use these methods

4. Use `Cache` facade to quickly access auto discovered cache service function

```java
Cache.startup();
Cache.put(key, object);
object = Cache.get(key);
Cache.evict(key);
Cache.shutdown();
```

### Configuration

1. Simple Cache service

There is nearly not configuration required to use Simple cache service. The only thing you can do is to Set the default ttl:

```java
CacheService cache = CacheServiceProvider.Impl.Simple.get();
cache.setDefaultTTL(120); // set the default ttl to 2 minutes
```

2. EhCache service

Just follow the [EhCache configuration document](http://ehcache.org/documentation/configuration/index) to configure EhCache service. Mainly what you need is to put a file named `ehcache.xml` into the class path.

3. Memcached service

The simplest way to configure memcached cache service is to set `memcached.host` system property to your memcached host. It should contains an ip address and a port number separated by `:`. If the port part is not provided, then default `11211` port is used. If your memcached service needs username/password to access, you can set those configurations in `memcached.username` and `memcached.password`.

Instead of using system properties, you can also set the memcached configuration in `memcached.properties` file which could be loaded from the class path.