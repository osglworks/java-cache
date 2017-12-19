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
