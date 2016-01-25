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
package org.jasig.portal.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Function;

/**
 * Utilities for working with concurrent maps
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public final class ConcurrentMapUtils {
    private ConcurrentMapUtils() {
    }
    
    /**
     * How putIfAbsent should work, returns the one value that actually ends up in the {@link ConcurrentMap}
     * 
     * @param map The map
     * @param key The key
     * @param value The value to put
     * @return The value that exists in the Map for this key at the point in time that {@link ConcurrentMap#putIfAbsent(Object, Object)} is called 
     */
    public static <K, V> V putIfAbsent(ConcurrentMap<K, V> map, K key, V value) {
        final V existingValue = map.putIfAbsent(key, value);
        if (existingValue == null) {
            return value;
        }
        
        return existingValue;
    }
    
    /**
     * Make a {@link ConcurrentMap} that when {@link ConcurrentMap#get(Object)} is called an null
     * is returned the creator function is called and the value returned is used with {@link ConcurrentMap#putIfAbsent(Object, Object)}.
     * The value that ends up in the {@link ConcurrentMap} is returned by the get function.
     */
    public static <K, V> ConcurrentMap<K, V> makeDefaultsMap(final Function<K, V> creator) {
        return new ConcurrentHashMap<K, V>() {
            @Override
            public V get(Object keyObj) {
                V value = super.get(keyObj);
                if (value == null) {
                    K key = (K)keyObj;
                    value = creator.apply(key);
                    final V existingValue = super.putIfAbsent(key, value);
                    if (existingValue != null) {
                        value = existingValue;
                    }
                }
                return value;
            }
        };
    }
}
