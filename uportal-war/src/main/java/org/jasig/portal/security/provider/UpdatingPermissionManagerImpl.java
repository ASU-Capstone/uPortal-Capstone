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
package org.jasig.portal.security.provider;

import org.jasig.portal.AuthorizationException;
import org.jasig.portal.security.IAuthorizationPrincipal;
import org.jasig.portal.security.IPermission;
import org.jasig.portal.security.IUpdatingPermissionManager;

/**
 * @author Dan Ellentuck (de3@columbia.edu)
 * @version $Revision$
 */
public class UpdatingPermissionManagerImpl extends PermissionManagerImpl implements IUpdatingPermissionManager
{
  /**
   * This constructor ensures that the PermissionManager will be created with an owner specified
   * @param newOwner the new owner
   * @param authService the authorization service
   */
  public UpdatingPermissionManagerImpl (String newOwner, AuthorizationImpl authService)
  {
    super(newOwner, authService);
  }
  
  /**
   * Add a new set of IPermission objects to the system.
   * @param newPermissions
   */
  public void addPermissions (IPermission[] newPermissions) throws AuthorizationException
  {
    getAuthorizationService().addPermissions(newPermissions);
  }

  /**
   * Retrieve an array of <code>IPermission</code> objects based on the given parameters.
   * Includes inherited <code>IPermissions</code>.  Any null parameters will be ignored.
   *
   * @param principal IAuthorizationPrincipal
   * @param activity String
   * @param target String
   * @return IPermission[]
   * @exception AuthorizationException
   */
  public IPermission[] getAllPermissions (IAuthorizationPrincipal principal, String activity, String target)
  throws AuthorizationException
  {
      return principal.getAllPermissions(getOwner(), activity, target);
  }

  /**
   * Retrieve an array of IPermission objects based on the given parameters. Any null parameters
   * will be ignored.
   *
   * @param principal IAuthorizationPrincipal
   * @param activity String
   * @param target String
   * @return IPermission[]
   * @exception AuthorizationException
   */
  public IPermission[] getPermissions (IAuthorizationPrincipal principal, String activity, String target)
  throws AuthorizationException
  {
    return getAuthorizationService().getUncachedPermissionsForPrincipal(principal, getOwner(), activity, target);
  }
  
  /**
   * Creates a new <code>IPermission</code> for the <code>principal</code> on behalf of the
   * owner of this <code>IPermissionManager</code>.
   *
   * @param principal IAuthorizationPrincipal
   */
  public IPermission newPermission(IAuthorizationPrincipal principal) throws AuthorizationException
  {
      return getAuthorizationService().newPermission(getOwner(), principal);
  }

  /**
   * Remove set of IPermission objects from the system.
   * @param oldPermissions
   */
  public void removePermissions (IPermission[] oldPermissions) throws AuthorizationException
  {
      getAuthorizationService().removePermissions(oldPermissions);
  }

  /**
   * Update a set of IPermission objects to the system.
   * @param changedPermissions
   */
  public void updatePermissions (IPermission[] changedPermissions) throws AuthorizationException
  {
      getAuthorizationService().updatePermissions(changedPermissions);
  }

}
