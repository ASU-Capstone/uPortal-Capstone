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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Populator that targets a Map 
 * 
 * @author Eric Dalquist
 * @param <K>
 * @param <V>
 */
public class MapPopulator<K, V> implements Populator<K, V> {
    private final Map<? super K, ? super V> map;
    
    public MapPopulator() {
        this.map = new LinkedHashMap<K, V>();
    }

    public MapPopulator(Map<? super K, ? super V> map) {
        this.map = map;
    }

    @Override
    public Populator<K, V> put(K k, V v) {
        this.map.put(k, v);
        return this;
    }
    
    @Override
    public Populator<K, V> putAll(Map<? extends K, ? extends V> m) {
        this.map.putAll(m);
        return this;
    }

    @SuppressWarnings("unchecked")
    public Map<K, V> getMap() {
        return (Map<K, V>) map;
    }
}