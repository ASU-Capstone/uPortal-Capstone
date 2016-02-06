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
package org.jasig.portal.security;

import java.util.Vector;

import org.jasig.portal.AuthorizationException;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IGroupMember;
import org.jasig.portal.portlet.om.IPortletDefinition;
import org.jasig.portal.portlet.om.PortletLifecycleState;

/**
 * @author Bernie Durfee, bdurfee@interactivebusiness.com
 * @author Dan Ellentuck, de3@columbia.edu
 * @version $Revision$
 *
 * An <code>IAuthorizationService</code> represents the portal's authorization service.
 * All requests for authorization activities ultimately come here.
 */
public interface IAuthorizationService
{
/**
 * Adds <code>IPermissions</code> to the service.
 * @param permissions IPermission[]
 * @exception AuthorizationException
 */
    public void addPermissions(IPermission[] permissions) throws AuthorizationException;
/**
 * Answers if the principal has permission to use the CONFIG PortletMode on this Channel.
 * @return boolean
 * @param principal IAuthorizationPrincipal
 * @param channelPublishId int
 * @exception AuthorizationException indicates authorization information could not be retrieved.
 */
    public boolean canPrincipalConfigure(IAuthorizationPrincipal principal, String channelPublishId)
    throws AuthorizationException;
/**
 * Answers if the principal has permission to manage this Channel.
 * @return boolean
 * @param principal IAuthorizationPrincipal
 * @param channelPublishId int
 * @exception AuthorizationException indicates authorization information could not be retrieved.
 */
    public boolean canPrincipalManage(IAuthorizationPrincipal principal, String channelPublishId)
    throws AuthorizationException;
/**
 * I'm not sure what this means (Dan).  Publish what?
 * @param principal IAuthorizationPrincipal
 * @return boolean
 * @exception AuthorizationException
 */
    public boolean canPrincipalManage(IAuthorizationPrincipal principal, PortletLifecycleState state, String categoryId)
    throws AuthorizationException;
/**
 * Answers if the principal has permission to render this Channel.
 * @return boolean
 * @param principal IAuthorizationPrincipal
 * @param channelPublishId int
 * @exception AuthorizationException indicates authorization information could not be retrieved.
 */
    public boolean canPrincipalRender(IAuthorizationPrincipal principal, String channelPublishId)
    throws AuthorizationException;

    /**
     * True if the principal has permission to browse this portlet.
     * @param principal principal
     * @param portletDefinitionId Portlet definition string ID of the portlet to check
     * @return True if the principal has permission to browse this portlet.
     */
    public boolean canPrincipalBrowse(IAuthorizationPrincipal principal, String portletDefinitionId);

    /**
     * True if the principal has permission to browse this portlet.
     * @param principal principal
     * @param portlet Portlet to check
     * @return True if the principal has permission to browse this portlet.
     */
    public boolean canPrincipalBrowse(IAuthorizationPrincipal principal, IPortletDefinition portlet);

    /**
 * Answers if the principal has permission to subscribe to this Channel.
 * @return boolean
 * @param principal IAuthorizationPrincipal
 * @param channelPublishId int
 * @exception AuthorizationException indicates authorization information could not be retrieved.
 */
    public boolean canPrincipalSubscribe(IAuthorizationPrincipal principal, String channelPublishId)
    throws AuthorizationException;
/**
 * Answers if the owner has given the principal permission to perform the activity on
 * the target.  Params <code>owner</code> and <code>activity</code> must be non-null.
 * If <code>target</code> is null, then target is not checked.
 * <br/>
 * NOTE:  Do not invoke this method if there is a more specific method implemented.  The more specific method
 * may have additional behavior.
 *
 * @return boolean
 * @param principal IAuthorizationPrincipal
 * @param owner java.lang.String
 * @param activity java.lang.String
 * @param target java.lang.String
 * @exception AuthorizationException indicates authorization information could not
 * be retrieved.
 */
    public boolean doesPrincipalHavePermission(
      IAuthorizationPrincipal principal,
      String owner,
      String activity,
      String target)
    throws AuthorizationException;
/**
 * Returns the <code>IPermissions</code> owner has granted this <code>Principal</code> for
 * the specified activity and target.  This includes inherited <code>IPermissions</code>.
 * Null parameters will be ignored, that is, all <code>IPermissions</code> matching the
 * non-null parameters are retrieved.  So, <code>getPermissions(principal,null, null, null)</code>
 * should retrieve all <code>IPermissions</code> for a <code>Principal</code>.
 *
 * @return org.jasig.portal.security.IPermission[]
 * @param principal IAuthorizationPrincipal
 * @param owner java.lang.String
 * @param activity java.lang.String
 * @param target java.lang.String
 * @exception AuthorizationException indicates authorization information could not
 * be retrieved.
 */
    public IPermission[] getAllPermissionsForPrincipal (
          IAuthorizationPrincipal principal,
      String owner,
      String activity,
      String target)
    throws AuthorizationException;
/**
 * Does this mean all channels the principal could conceivably subscribe
 * to or all channels principal is specifically authorized to subscribe to,
 * or what?
 * 
 * @param principal IAuthorizationPrincipal
 * @return Vector (of channels?)
 * @exception AuthorizationException indicates authorization information could not
 */
    public Vector getAuthorizedChannels(IAuthorizationPrincipal principal)
    throws AuthorizationException;

/**
 * @return org.jasig.portal.groups.IGroupMember
 * @param principal org.jasig.portal.security.IAuthorizationPrincipal
 */
    public IGroupMember getGroupMember(IAuthorizationPrincipal principal)
    throws GroupsException;

/**
 * Returns the <code>IPermissions</code> owner has granted for the specified activity
 * and target.  Null parameters will be ignored, that is, all <code>IPermissions</code>
 * matching the non-null parameters are retrieved.
 *
 * @return org.jasig.portal.security.IPermission[]
 * @param owner java.lang.String
 * @param activity java.lang.String
 * @param target java.lang.String
 * @exception AuthorizationException indicates authorization information could not
 * be retrieved.
 */
    public IPermission[] getPermissionsForOwner(String owner, String activity, String target)
    throws AuthorizationException;
/**
 * Returns the <code>IPermissions</code> owner has granted this <code>Principal</code> for
 * the specified activity and target.  Null parameters will be ignored, that is, all
 * <code>IPermissions</code> matching the non-null parameters are retrieved.  So,
 * <code>getPermissions(principal,null, null, null)</code> should retrieve all <code>IPermissions</code>
 * for a <code>Principal</code>.
 *
 * @return org.jasig.portal.security.IPermission[]
 * @param principal IAuthorizationPrincipal
 * @param owner java.lang.String
 * @param activity java.lang.String
 * @param target java.lang.String
 * @exception AuthorizationException indicates authorization information could not
 * be retrieved.
 */
    public IPermission[] getPermissionsForPrincipal (
      IAuthorizationPrincipal principal,
      String owner,
      String activity,
      String target)
    throws AuthorizationException;
/**
 * Creates a new <code>IPermission</code> for the <code>principal</code> on behalf of the
 * <code>owner</code>.
 *
 * @param owner String
 * @param principal IAuthorizationPrincipal
 */
    public IPermission newPermission(String owner, IAuthorizationPrincipal principal)
    throws AuthorizationException;
/**
 * @param key java.lang.String
 * @return org.jasig.portal.security.IPermissionManager
 * @exception org.jasig.portal.AuthorizationException
 */
    public IPermissionManager newPermissionManager(String key) throws AuthorizationException;
/**
 * Factory method for an <code>IAuthorizationPrincipal</code>.  This type wraps an underlying
 * entity so that the authorization system can treat all principals alike.
 * @param key String
 * @param type Class
 * @return IAuthorizationPrincipal
 */
    public IAuthorizationPrincipal newPrincipal(String key, Class type);

/**
 * Converts an <code>IGroupMember</code> into an <code>IAuthorizationPrincipal</code>.
 * @return org.jasig.portal.security.IAuthorizationPrincipal
 * @param groupMember org.jasig.portal.groups.IGroupMember
 */
    public IAuthorizationPrincipal newPrincipal(IGroupMember groupMember)
    throws GroupsException;
/**
 * @return org.jasig.portal.security.IUpdatingPermissionManager
 * @param key java.lang.String
 * @exception org.jasig.portal.AuthorizationException
 */
    public IUpdatingPermissionManager newUpdatingPermissionManager(String key)
    throws AuthorizationException;
/**
 * Removes <code>IPermissions</code> from the service.
 * @param permissions IPermission[]
 * @exception AuthorizationException
 */
    public void removePermissions(IPermission[] permissions) throws AuthorizationException;

/**
 * Updates <code>IPermissions</code> in the service.
 * @param permissions IPermission[]
 * @exception AuthorizationException
 */
    public void updatePermissions(IPermission[] permissions) throws AuthorizationException;

/**
 * Returns the <code>IAuthorizationPrincipal</code> associated with the <code>IPermission</code>.
 * @param permission IPermission
 */
    public IAuthorizationPrincipal getPrincipal(IPermission permission)
    throws AuthorizationException;

/**
 * Returns a <code>String</code> used to represent the <code>IAuthorizationPrincipal</code>.
 * @param principal IAuthorizationPrincipal
 */
    public String getPrincipalString(IAuthorizationPrincipal principal);

/**
 * Answers if the owner has given the principal permission to perform the activity on
 * the target, as evaluated by the policy.  Params <code>policy</code>, <code>owner</code> 
 * and <code>activity</code> must be non-null.  If <code>target</code> is null, then 
 * target is not checked.
 * <br/>
 * NOTE:  Do not invoke this method if there is a more specific method implemented.  The more specific method
 * may have additional behavior.
p *
 * @return boolean
 * @param principal IAuthorizationPrincipal
 * @param owner java.lang.String
 * @param activity java.lang.String
 * @param target java.lang.String
 * @exception AuthorizationException indicates authorization information could not
 * be retrieved.
 */
    public boolean doesPrincipalHavePermission(
      IAuthorizationPrincipal principal,
      String owner,
      String activity,
      String target,
      IPermissionPolicy policy)
    throws AuthorizationException;
}
