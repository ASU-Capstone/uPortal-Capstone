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
package org.jasig.portal.rendering.xslt;

import org.jasig.portal.IUserPreferencesManager;
import org.jasig.portal.layout.IStylesheetUserPreferencesService;
import org.jasig.portal.layout.om.IStylesheetDescriptor;
import org.jasig.portal.url.IPortalRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Return {@link IUserPreferencesManager#getThemeStylesheetDescriptorId()}
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ThemeTransformerSource extends BaseTransformerSource {
    @Autowired
    private IStylesheetUserPreferencesService stylesheetUserPrefService;
    
    @Autowired
    private IPortalRequestUtils reqUtils;
    
    @Override
    protected long getStylesheetDescriptorId(IUserPreferencesManager preferencesManager) {
        IStylesheetDescriptor descriptor = stylesheetUserPrefService
                .getStylesheetDescriptor( reqUtils.getCurrentPortalRequest(),
                        IStylesheetUserPreferencesService.PreferencesScope.THEME);
        return descriptor.getId();
    }
}
