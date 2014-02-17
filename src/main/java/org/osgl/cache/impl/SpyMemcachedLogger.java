package org.osgl.cache.impl;

import net.spy.memcached.compat.log.AbstractLogger;
import net.spy.memcached.compat.log.Level;
import org.osgl.logging.L;
import org.osgl.logging.Logger;
import org.osgl.util.E;
import org.osgl.util.S;

/**
 * Created by luog on 17/02/14.
 */
public class SpyMemcachedLogger extends AbstractLogger {

    private Logger logger = null;

    public SpyMemcachedLogger(String name) {
        super(name);
        logger = L.get(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void log(Level level, Object message, Throwable e) {
        String s = S.string(message);
        switch (level) {
            case TRACE:
                logger.trace(e, s);
                break;
            case DEBUG:
                logger.debug(e, s);
                break;
            case INFO:
                logger.info(e, s);
                break;
            case WARN:
                logger.warn(e, s);
                break;
            case ERROR:
                logger.error(e, s);
                break;
            case FATAL:
                logger.fatal(e, s);
            default:
                throw E.unexpected("Error level not recognized: %s", level);
        }
    }
}
