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
package org.jasig.portal.io.xml.eventaggr;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.jasig.portal.utils.ComparableExtractingComparator;

/**
 * Sort group mappings
 * 
 * @author Eric Dalquist
 */
public class ExternalAggregatedGroupMappingComparator implements Comparator<ExternalAggregatedGroupMapping> {
    private final ComparatorChain chain;
    
    public static final ExternalAggregatedGroupMappingComparator INSTANCE = new ExternalAggregatedGroupMappingComparator();
    
    @SuppressWarnings("unchecked")
    private ExternalAggregatedGroupMappingComparator() {
        chain = new ComparatorChain(Arrays.asList(
                new ComparableExtractingComparator<ExternalAggregatedGroupMapping, String>() {
                    @Override
                    protected String getComparable(ExternalAggregatedGroupMapping o) {
                        return o.getGroupService();
                    }
                },
                new ComparableExtractingComparator<ExternalAggregatedGroupMapping, String>() {
                    @Override
                    protected String getComparable(ExternalAggregatedGroupMapping o) {
                        return o.getGroupName();
                    }
                }
        ));
    }
    
    @Override
    public int compare(ExternalAggregatedGroupMapping o1, ExternalAggregatedGroupMapping o2) {
        return chain.compare(o1, o2);
    }
}
