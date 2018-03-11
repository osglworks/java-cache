# OSGL Cache Change Log

1.3.0 -
- catch up to osgl-tool-1.7
- Implement `SimpleCacheService` with `SoftReference` #3
- Potential `NullPointerException` in `SimpleCacheService` implementation #2

1.2.0 - 
- catch up to osgl-tool-1.6
- support configure classloader #1

1.1.0 - 19/Dec/2017
- catch up osgl-tool-1.5.0
- support incr and decr operation

1.0.2
- take out version range (see https://issues.apache.org/jira/browse/MNG-3092)

1.0.1
- Use version range for osgl dependencies

1.0.0
- baseline from 0.5.0

0.5.0-SNAPSHOT
- update tool to 0.10.0

0.4.1-SNAPSHOT
- CacheServiceProvider.valueOfIgnoreCase: should accept Auto value

0.4.0-SNAPSHOT
- Upgrade to osgl-tool 0.9

0.3.2-SNAPSHOT
- Upgrade to osgl-tool 0.7.1-SNAPSHOT

0.3.1-SNAPSHOT
- Fix issue: SimpleCacheService.put(xxx) failed to update timestamp of the item thus will not refresh the timeout

0.3-SNAPSHOT
- upgrade to osgl-tool 0.4-SNAPSHOT

0.2.1-SNAPSHOT:
- allow configure cache service provider implementation by using
  System Property "osgl.cache.impl"
