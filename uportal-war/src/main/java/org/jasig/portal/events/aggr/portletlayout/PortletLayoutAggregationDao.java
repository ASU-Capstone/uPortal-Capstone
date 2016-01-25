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
package org.jasig.portal.events.aggr.portletlayout;

import java.util.List;

import org.jasig.portal.events.aggr.AggregationInterval;
import org.jasig.portal.events.aggr.BaseAggregationDao;
import org.jasig.portal.events.aggr.groups.AggregatedGroupMapping;
import org.joda.time.DateTime;

/**
 * DAO used to query information about portlet layout aggregates
 *
 * @author Chris Waymire <cwaymire@unicon.net>
 */
public interface PortletLayoutAggregationDao<T extends PortletLayoutAggregation> extends BaseAggregationDao<T,PortletLayoutAggregationKey> {
    /**
     * Get a list of all aggregations for the date range and groups.
     * 
     * @param start Start date (inclusive)
     * @param end End date (exclusive)
     * @param interval The aggregation interval
     * @param aggregatedGroupMapping The groups to get data for
     * @param aggregatedGroupMappings The groups to get data for
     * @return
     */
    List<T> getAggregationsForAllPortlets(DateTime start, DateTime end, AggregationInterval interval,
            AggregatedGroupMapping aggregatedGroupMapping, AggregatedGroupMapping... aggregatedGroupMappings);
}
