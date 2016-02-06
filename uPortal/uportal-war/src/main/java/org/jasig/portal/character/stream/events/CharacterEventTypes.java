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
package org.jasig.portal.character.stream.events;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public enum CharacterEventTypes {
    /**
     * @see CharacterDataEvent
     */
    CHARACTER,
    /**
     * @see PortletContentPlaceholderEvent
     */
    PORTLET_CONTENT,
    /**
     * @see PortletTitlePlaceholderEvent
     */
    PORTLET_TITLE,
    /**
     * @see PortletNewItemCountPlaceholderEvent
     */
    PORTLET_NEW_ITEM_COUNT,
    /**
     * @see PortletLinkPlaceholderEvent
     */
    PORTLET_LINK,
    /**
     * @see PortletHelpPlaceholderEvent
     * @deprecated As of Jul 2015 I don't think this is actually used
     */
    PORTLET_HELP,
    /**
     * @see PortletHeaderPlaceholderEvent
     */
    PORTLET_HEADER,
    /**
     * @see org.jasig.portal.json.rendering.JsonLayoutPlaceholderEvent
     */
    JSON_LAYOUT,
    /**
     * @see PortletAnalyticsDataPlaceholderEvent 
     */
    PORTLET_ANALYTICS_DATA,
    /**
     * @see PageAnalyticsDataPlaceholderEvent 
     */
    PAGE_ANALYTICS_DATA;
}
