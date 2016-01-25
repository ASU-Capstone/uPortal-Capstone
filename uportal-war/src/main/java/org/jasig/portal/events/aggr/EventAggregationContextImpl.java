/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.events.aggr;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic context impl
 * 
 * @author Eric Dalquist
 */
class EventAggregationContextImpl implements EventAggregationContext {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final Map<Object, Object> attributes = new HashMap<Object, Object>();
    
    @Override
    public void setAttribute(Object key, Object value) {
        final Object old = this.attributes.put(key, value);
        if (old != null) {
            logger.warn("Replaced existing event aggr context for key={}", key);
        }
    }

    @Override
    public <T> T getAttribute(Object key) {
        return (T)attributes.get(key);
    }

}
