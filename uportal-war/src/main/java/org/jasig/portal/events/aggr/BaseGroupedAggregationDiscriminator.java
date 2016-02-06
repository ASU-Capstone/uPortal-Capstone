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

import org.jasig.portal.events.aggr.groups.AggregatedGroupMapping;


/**
 * Base of all aggregation discriminators used to organize aggregation results into different report columns
 * 
 * @author James Wennmacher
 * @version $Revision$
 */
public interface BaseGroupedAggregationDiscriminator {

    /**
     * @return The group this aggregation is for, null if it is for all users
     */
    AggregatedGroupMapping getAggregatedGroup();

}
