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

import java.io.Serializable;

import org.jasig.portal.events.aggr.groups.AggregatedGroupMapping;

/**
 * Fields that make up the primary key for a {@link BaseAggregation}. {@link BaseAggregation}
 * subclasses may define their own extension of this interface if additional fields are needed
 * 
 * @author Eric Dalquist
 */
public interface BaseAggregationKey extends Serializable {
    /**
     * @return The day the aggregation is for
     */
    DateDimension getDateDimension();
    
    /**
     * @return The time of day the aggregation is for
     */
    TimeDimension getTimeDimension();
    
    /**
     * @return The interval the aggregation is for
     */
    AggregationInterval getInterval();
    
    /**
     * @return The group this aggregation is for, null if it is for all users
     */
    AggregatedGroupMapping getAggregatedGroup();
}