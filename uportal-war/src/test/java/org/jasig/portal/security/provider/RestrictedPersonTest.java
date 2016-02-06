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

import java.util.Enumeration;

import org.jasig.portal.security.IAdditionalDescriptor;
import org.jasig.portal.security.IOpaqueCredentials;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.IPrincipal;
import org.jasig.portal.security.ISecurityContext;
import org.jasig.portal.security.PortalSecurityException;

import junit.framework.TestCase;

/**
 * Testcase for RestrictedPerson.
 * @author andrew.petro@yale.edu
 * @version $Revision$ $Date$
 */
public class RestrictedPersonTest extends TestCase {

    private IPerson person;
    
    private RestrictedPerson restrictedPerson;
    
    protected void setUp() {
        IPerson fullPerson = new PersonImpl();
        fullPerson.setAttribute("att1", "val1");
        fullPerson.setAttribute("att2", "val2");
        
        fullPerson.setFullName("George Washington");
        
        fullPerson.setID(27);
        
        fullPerson.setSecurityContext(new DummySecurityContext());
        
        
        this.person = fullPerson;
        this.restrictedPerson = new RestrictedPerson(fullPerson);
    }
    
    protected void tearDown() {
        this.person = null;
        this.restrictedPerson = null;
    }
    
    /**
     * Test that getSecurityContext of RestrictedPerson
     * returns null.
     */
    public void testGetSecurityContext() {
        assertNull(this.restrictedPerson.getSecurityContext());
    }
    
    /**
     * Test that setSecurityContext does not change the security context.
     */
    public void testSetSecurityContext() {
        ISecurityContext baselineContext = this.person.getSecurityContext();
        assertNotNull(baselineContext);
        
        assertNull(this.restrictedPerson.getSecurityContext());
        
        this.restrictedPerson.setSecurityContext(new DummySecurityContext());
        assertNull(this.restrictedPerson.getSecurityContext());
        
        assertSame(baselineContext, this.person.getSecurityContext());
    }
    
    /**
     * Test that the getEntityIdentifier() method of RestrictedPerson
     * reads through to the underlying IPerson's entity identifier.
     */
    public void testGetEntityIdentifier() {
        assertNotNull(this.person.getEntityIdentifier());
        assertSame(this.person.getEntityIdentifier(), this.restrictedPerson.getEntityIdentifier());
    }
    
    /**
     * Test that the setAttribute() method of RestrictedPerson writes through
     * to the underlying IPerson.
     */
    public void testSetAttribute() {
        
        // test that new attributes write
        
        assertNull(this.person.getAttribute("notSet"));
        assertNull(this.restrictedPerson.getAttribute("notSet"));
        
        this.restrictedPerson.setAttribute("notSet", "nowSet");
        
        assertEquals("nowSet", this.person.getAttribute("notSet"));
        assertEquals("nowSet", this.restrictedPerson.getAttribute("notSet"));

        
        // test that existing attribute are overwritten
        
        assertEquals("val1", this.person.getAttribute("att1"));
        assertEquals("val1", this.restrictedPerson.getAttribute("att1"));
        
        this.restrictedPerson.setAttribute("att1", "newValue");
        
        assertEquals("newValue", this.person.getAttribute("att1"));
        assertEquals("newValue", this.restrictedPerson.getAttribute("att1"));
        
    }

    /**
     * Test that the setFullName method of RestrictedPerson writes through
     * to the underlying IPerson.
     */
    public void testSetFullname() {
        assertEquals("George Washington", this.restrictedPerson.getFullName());
        assertEquals("George Washington", this.person.getFullName());

        this.restrictedPerson.setFullName("Peter Furmonavicius");
        
        assertEquals("Peter Furmonavicius", this.restrictedPerson.getFullName());
        assertEquals("Peter Furmonavicius", this.person.getFullName());
    }
    
    /**
     * Test that the RestrictedPerson setID method writes through
     * to the underlying IPerson.
     */
    public void testSetID() {
        assertEquals(27, this.person.getID());
        assertEquals(27, this.restrictedPerson.getID());
        
        this.restrictedPerson.setID(12);
        
        assertEquals(12, this.person.getID());
        assertEquals(12, this.restrictedPerson.getID());
    }
    
    /**
     * A dummy ISecurityContext implementation.
     * Useful as a non-null ISecurityContext, but otherwise no methods do
     * anything.
     */
    private class DummySecurityContext 
        implements ISecurityContext {

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#getAuthType()
         */
        public int getAuthType() {
            return 0;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#getPrincipalInstance()
         */
        public IPrincipal getPrincipalInstance() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#getOpaqueCredentialsInstance()
         */
        public IOpaqueCredentials getOpaqueCredentialsInstance() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#authenticate()
         */
        public void authenticate() throws PortalSecurityException {
            // do nothing -- dummy implementation
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#getPrincipal()
         */
        public IPrincipal getPrincipal() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#getOpaqueCredentials()
         */
        public IOpaqueCredentials getOpaqueCredentials() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#getAdditionalDescriptor()
         */
        public IAdditionalDescriptor getAdditionalDescriptor() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#isAuthenticated()
         */
        public boolean isAuthenticated() {
            return false;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#getSubContext(java.lang.String)
         */
        public ISecurityContext getSubContext(String ctx) throws PortalSecurityException {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#getSubContexts()
         */
        public Enumeration getSubContexts() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#getSubContextNames()
         */
        public Enumeration getSubContextNames() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.security.ISecurityContext#addSubContext(java.lang.String, org.jasig.portal.security.ISecurityContext)
         */
        public void addSubContext(String name, ISecurityContext ctx) throws PortalSecurityException {
            // do nothing -- dummy implementation
        }
        
    }
    
}
