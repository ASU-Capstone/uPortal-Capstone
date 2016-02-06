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


/**
 * Manages processing of portal event data
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface IPortalEventProcessingManager {
    
    /**
     * Make sure {@link DateDimension} and {@link TimeDimension} objects exist for a reasonable distance into the future
     * @return If the dimensions were correctly populated
     * @see PortalEventDimensionPopulator#doPopulateDimensions()
     */
    boolean populateDimensions();

    /**
     * Requests that raw event data be aggregated
     * @return If the available events were aggregated
     * @see PortalRawEventsAggregator#doAggregateRawEvents()
     * @see PortalRawEventsAggregator#evictAggregates(java.util.Map)
     * @see PortalRawEventsAggregator#doCloseAggregations()
     */
    boolean aggregateRawEvents();

    /**
     * Requests that raw event data be purged
     * @return If expired events were purged
     * @see PortalEventPurger#doPurgeRawEvents()
     */
    boolean purgeRawEvents();

    /**
     * Requests that event session data be purged
     * @return If expired event sessions were purged
     * @see PortalEventSessionPurger#doPurgeEventSessions()
     */
    boolean purgeEventSessions();

}