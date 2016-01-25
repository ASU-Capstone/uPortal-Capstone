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
package org.jasig.portal.portlet.container.properties;

import javax.servlet.http.HttpServletRequest;

import org.jasig.portal.portlet.om.IPortletWindow;
import org.jasig.portal.portlet.rendering.IPortletRenderer;
import org.springframework.stereotype.Service;

/**
 * @author Jen Bourey
 * @version $Revision$
 */
@Service
public class NewItemCountRequestPropertiesManager extends BaseRequestPropertiesManager {
    /**
     * Use {@link IPortletRenderer#NEW_ITEM_COUNT_PROPERTY}
     */
    @Deprecated
    protected static final String NEW_ITEM_COUNT_PROPERTY = "newItemCount";

    @Override
    public boolean setResponseProperty(HttpServletRequest portletRequest, IPortletWindow portletWindow, String property, String value) {
        if (NEW_ITEM_COUNT_PROPERTY.equals(property) || IPortletRenderer.NEW_ITEM_COUNT_PROPERTY.equals(property)) {
            portletRequest.setAttribute(IPortletRenderer.ATTRIBUTE__PORTLET_NEW_ITEM_COUNT, value);
            return true;
        }
        
        return false;
    }
}
