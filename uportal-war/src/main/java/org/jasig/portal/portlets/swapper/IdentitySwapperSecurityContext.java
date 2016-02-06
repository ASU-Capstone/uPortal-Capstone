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
package org.jasig.portal.portlets.swapper;

import java.util.Collections;
import java.util.Enumeration;

import org.jasig.portal.security.IAdditionalDescriptor;
import org.jasig.portal.security.IOpaqueCredentials;
import org.jasig.portal.security.IPrincipal;
import org.jasig.portal.security.ISecurityContext;
import org.jasig.portal.security.PortalSecurityException;


/**
 * Security context used exclusively for doing identity-swaps
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class IdentitySwapperSecurityContext implements ISecurityContext {
    private static final long serialVersionUID = 1L;

    private final IPrincipal principal;

    public IdentitySwapperSecurityContext(IPrincipal principal) {
        this.principal = principal;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#addSubContext(java.lang.String, org.jasig.portal.security.ISecurityContext)
     */
    public void addSubContext(String name, ISecurityContext ctx) throws PortalSecurityException {
        throw new UnsupportedOperationException("This context does not support chaining.");
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#authenticate()
     */
    public void authenticate() throws PortalSecurityException {
        //Do Nothing
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#getAdditionalDescriptor()
     */
    public IAdditionalDescriptor getAdditionalDescriptor() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#getAuthType()
     */
    public int getAuthType() {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#getOpaqueCredentials()
     */
    public IOpaqueCredentials getOpaqueCredentials() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#getOpaqueCredentialsInstance()
     */
    public IOpaqueCredentials getOpaqueCredentialsInstance() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#getPrincipal()
     */
    public IPrincipal getPrincipal() {
        return this.principal;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#getPrincipalInstance()
     */
    public IPrincipal getPrincipalInstance() {
        return this.principal;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#getSubContext(java.lang.String)
     */
    public ISecurityContext getSubContext(String ctx) throws PortalSecurityException {
        return null;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#getSubContextNames()
     */
    public Enumeration getSubContextNames() {
        return Collections.enumeration(Collections.emptySet());
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#getSubContexts()
     */
    public Enumeration getSubContexts() {
        return Collections.enumeration(Collections.emptySet());
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.ISecurityContext#isAuthenticated()
     */
    public boolean isAuthenticated() {
        //Always says the user is authenticated
        return true;
    }
}
