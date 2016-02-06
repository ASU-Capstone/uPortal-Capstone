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
package org.jasig.portal.events.aggr.portletexec;

import org.jasig.portal.events.aggr.BaseGroupedAggregationDiscriminator;
import org.jasig.portal.events.aggr.portletexec.PortletExecutionAggregationKey.ExecutionType;
import org.jasig.portal.events.aggr.portlets.AggregatedPortletMapping;

/**
 * Interface to separate Portlet Execution aggregation events into separate columns for reporting purposes.
 *
 * @author James Wennmacher, jameswennmacher@gmail.com
 */
public interface PortletExecutionAggregationDiscriminator extends BaseGroupedAggregationDiscriminator {
    /**
     * @return The name of the tab
     */
    AggregatedPortletMapping getPortletMapping();
    
    /**
     * @return The type of portlet execution 
     */
    ExecutionType getExecutionType();
    
}
