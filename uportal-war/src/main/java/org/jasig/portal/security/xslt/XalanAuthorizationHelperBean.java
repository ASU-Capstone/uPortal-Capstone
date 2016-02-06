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
package org.jasig.portal.security.xslt;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.portlet.om.IPortletDefinition;
import org.jasig.portal.portlet.registry.IPortletDefinitionRegistry;
import org.jasig.portal.security.IAuthorizationPrincipal;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.IPersonManager;
import org.jasig.portal.services.AuthorizationService;
import org.jasig.portal.services.GroupService;
import org.jasig.portal.url.IPortalRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Spring managed version of the Xalan Elements helper class used during portal XSL
 * transformations.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
@Service
public class XalanAuthorizationHelperBean implements IXalanAuthorizationHelper {
    protected final Log logger = LogFactory.getLog(this.getClass());

    private IPortletDefinitionRegistry portletDefinitionRegistry;
    private IPortalRequestUtils portalRequestUtils;
    private IPersonManager personManager;
        
    /**
     * @param portletDefinitionRegistry the portletDefinitionRegistry to set
     */
    @Autowired
    public void setPortletDefinitionRegistry(IPortletDefinitionRegistry portletDefinitionRegistry) {
        this.portletDefinitionRegistry = portletDefinitionRegistry;
    }

    @Autowired
    public void setPortalRequestUtils(IPortalRequestUtils portalRequestUtils) {
        this.portalRequestUtils = portalRequestUtils;
    }

    @Autowired(required = true)
    public void setPersonManager(IPersonManager personManager) {
        this.personManager = personManager;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.xslt.IAuthorizationHelper#canRender(java.lang.String, java.lang.String)
     */
    @Override
    public boolean canRender(final String userName, final String fname) {
        if (userName == null || fname == null) {
            return false;
        }
        
        final IAuthorizationPrincipal userPrincipal = this.getUserPrincipal(userName);
        if (userPrincipal == null) {
            return false;
        }
        
        final String portletId;
        try {
            final IPortletDefinition portletDefinition = this.portletDefinitionRegistry.getPortletDefinitionByFname(fname);
            if (portletDefinition == null) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("No PortletDefinition for fname='" + fname + "', returning false.");
                }

                return false;
            }
            
            portletId = portletDefinition.getPortletDefinitionId().getStringId();
        }
        catch (Exception e) {
            this.logger.warn("Could not find PortletDefinition for fname='" + fname + "' while checking if user '" + userName + "' can render it. Returning FALSE.", e);
            return false;
        }
        
        return userPrincipal.canRender(portletId);
    }

    @Override
    public boolean hasPermission(final String owner, final String activity, final String target) {
        
        // owner & activity are required (but not target)
        if (owner == null || activity == null) {
            return false;
        }

        final HttpServletRequest currentRequest = portalRequestUtils.getCurrentPortalRequest();
        final IPerson currentUser = personManager.getPerson((HttpServletRequest) currentRequest);
        final IAuthorizationPrincipal authPrincipal = this.getUserPrincipal(currentUser.getUserName());
        
        final boolean rslt = authPrincipal != null
                ? authPrincipal.hasPermission(owner, activity, target)
                : false;
        return rslt;

    }

    protected IAuthorizationPrincipal getUserPrincipal(final String userName) {
        final IEntity user = GroupService.getEntity(userName, IPerson.class);
        if (user == null) {
            return null;
        }
        
        final AuthorizationService authService = AuthorizationService.instance();
        return authService.newPrincipal(user);
    }

}
