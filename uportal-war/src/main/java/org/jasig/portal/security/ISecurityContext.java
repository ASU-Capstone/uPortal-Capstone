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

import java.io.Serializable;
import java.util.Enumeration;

/**
 * This is the main interface for the JASIG portal effort's security
 * mechanism. We endeavor here to provide considerable encapsulation of
 * the data we are trying to present.
 *
 * @author Andrew Newman, newman@yale.edu
 * @version $Revision$
 * @author Don Fracapane (df7@columbia.edu)
 * Added getSubContextNames() to support principal and credential tokens
 */
public interface ISecurityContext extends Serializable {

  /**
   * Returns the canonical authentication type for this flavor of
   * authentication. Each value returned should be either a globally registered
   * auth flavor or a local variant.
   *
   * @return The unique authentication value identifier. Values with the
   * high order 16 bits clear are local (0x0000 - 0x00FF) where values with the
   * high order 16 bits set (0xFF00 - 0xFFFF are foundation types distributed
   * by JASIG. All other should be registered and globally unique.
   */
  public int getAuthType();

  /**
   * Returns an empty object reference to an object implementing the
   * Principal interface. By operating on this returned object the
   * implementation class for the credentials type will be able to access
   * any values set in the instance without exposing an interface method that
   * would allow others (inappropriate) access to the fields.
   *
   * @return An empty principal container.
   *
   * @see IPrincipal
   */
   public IPrincipal getPrincipalInstance();

  /**
   * Returns an empty object reference to an object implementing the
   * IOpaqueCredentials interface. By operating on this returned object
   * the implementation class for the credentials type will be able to
   * access any values set in the Opaque credentials without exposing an
   * interface method that would allow others to access the fields.
   *
   * @return An empty credentials container.
   *
   * @see IOpaqueCredentials
   */
  public IOpaqueCredentials getOpaqueCredentialsInstance();

  /**
   * Performs the operation of authentication. To perform this operation, the
   * values set in the Principal object (whose reference is
   * returned by <code>getPrincipalInstance()</code>) and the
   * OpaqueCredentials object (whose reference is returned
   * by <code>getOpaqueCredentialsInstance()</code>).
   *
   *  @see #getPrincipalInstance
   *  @see #getOpaqueCredentialsInstance
   */
  public void authenticate() throws PortalSecurityException;

  /**
   * Returns the currently authenticated principal if we are currently
   * authenticated. Note that merely testing this for a non-null pointer
   * is not sufficient to verify authenticated status. The isAuthenticated()
   * call should be used. In some authentication schemes, an asynchronous
   * event could potentially change one's authentication status.
   *
   * @return The currently authenticated principal.
   */
  public IPrincipal getPrincipal();

  /**
   * Returns any credentials that an authenticated principal currently
   * has. Note that opaque credentials don't have any methods for examination
   * of the credential's contents. This call would primarily be useful to
   * chain authentication manually within the same authentication scheme.
   *
   * @return The currently authenticated credentials object.
   *
   * @see IOpaqueCredentials
   */
  public IOpaqueCredentials getOpaqueCredentials();

  /**
   * Returns any additional descriptor information that might have been acquired
   * during the process of authentication. Note that this interface has no
   * methods and the object returned will have to be cast to some concrete
   * type or alternate interface to be useful.
   *
   * @return An object containing any additional descriptor information.
   *
   * @see IAdditionalDescriptor
   */
  public IAdditionalDescriptor getAdditionalDescriptor();

  /**
   * Returns a boolean status as to whether the descriptor corresponds to an
   * authenticated principal. Note that the get(Principal|OpaqueCredentials)
   * calls return null until isAuthenticated first returns <code>true</code>.
   */
  public boolean isAuthenticated();

  /**
   * Returns an <code>ISecurityContext</code> for the named subservient security
   * context.
   *
   * @return The security context object reference associated with the
   * name specified as the first parameter.
   *
   * @param ctx The non-compound name of the subservient security context.
   */
  public ISecurityContext getSubContext(String ctx) throws PortalSecurityException;

  /**
   * Returns an enumeration of the security contexts currently registered as
   * being subservient to this one.
   *
   * @return The enumeration object containing all of the contexts.
   */
  public Enumeration getSubContexts();

  /**
   * Returns an enumeration of the names of the security contexts currently
   * registered as being subservient to this one.
   *
   * @return The enumeration object containing all of the subcontext names.
   */
  public Enumeration getSubContextNames();

  /**
   * Adds a named sub context to the list of subservient subcontexts.
   *
   * @param name The non-compound name of the subservient context. Note that
   * under normal circumstances the establishment of the InitialSecurityContext
   * will automatically register all subcontext.
   *
   * @param ctx The security context object to register.
   *
   */
  public void addSubContext(String name, ISecurityContext ctx)
      throws PortalSecurityException;
}