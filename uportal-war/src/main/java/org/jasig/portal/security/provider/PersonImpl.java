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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.ISecurityContext;
import org.jasig.portal.security.PersonFactory;

/**
 * This is a reference IPerson implementation.
 * @author Adam Rybicki, arybicki@unicon.net
 * @version $Revision$
 */
public class PersonImpl implements IPerson {
    private static final long serialVersionUID = 1L;

    protected ConcurrentMap<String, List<Object>> userAttributes = null;
    protected String m_FullName = null;
    protected int m_ID = -1;
    protected ISecurityContext m_securityContext = null;
    protected EntityIdentifier m_eid = new EntityIdentifier(null, IPerson.class);
    protected boolean entityIdentifierSet = false;

    public ISecurityContext getSecurityContext() {
        return m_securityContext;
    }

    public void setSecurityContext(ISecurityContext securityContext) {
        m_securityContext = securityContext;
    }

    /**
     * Returns an attribute for a key.  For objects represented as strings,
     * a <code>java.lang.String</code> will be returned.  Binary values will
     * be represented as byte arrays.
     * @param key the attribute name.
     * @return value the attribute value identified by the key.
     */
    public Object getAttribute(String key) {
        if (this.userAttributes == null) {
            return null;
        }

        final List<Object> values = this.userAttributes.get(key);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }

        return null;
    }

    /**
     * Returns multiple attributes for a key.  If only one
     * value exists, it will be returned in an array of size one.
     * @param key the attribute name
     * @return the array of attribute values identified by the key
     */
    public Object[] getAttributeValues(String key) {
        if (this.userAttributes == null) {
            return null;
        }

        final List<Object> values = this.userAttributes.get(key);
        if (values != null) {
            return values.toArray();
        }

        return null;
    }

    /**
     * Provides access to this {@link org.jasig.portal.security.provider.PersonImpl}'s private copy of the attributes
     * attached to this {@link IPerson}.  Changes to the map will affect the attributes directly.  (Perhaps we'd rather
     * do a defensive copy?)
     */
    public Map<String,List<Object>> getAttributeMap() {
        final Map<String,List<Object>> attrMap = this.userAttributes;
        return attrMap;
    }

    /**
     * Sets the specified attribute to a value.
     *
     * Reference implementation checks for the setting of the username attribute
     * and updates the EntityIdentifier accordingly
     *
     * @param key Attribute's name
     * @param value Attribute's value
     */
    public void setAttribute(String key, Object value) {
        if (value == null) {
            this.setAttribute(key, (List<Object>)null);
        }
        else {
            this.setAttribute(key, Collections.singletonList(value));
        }
    }
    
    public void setAttribute(String key, List<Object> value) {
        if (this.userAttributes == null) {
            this.userAttributes = new ConcurrentHashMap<String, List<Object>>();
        }

        if (value != null) {
            this.userAttributes.put(key, value);
        }
        else {
            this.userAttributes.remove(key);
        }
        
        if (!this.entityIdentifierSet && key.equals(IPerson.USERNAME)) {
            final Object userName = value != null && value.size() > 0 ? value.get(0) : null;
            this.m_eid = new EntityIdentifier(String.valueOf(userName), IPerson.class);
        }
    }

    /**
     * Sets the specified attributes. Uses {@link #setAttribute(String, Object)}
     * to set each.
     *
     * @see org.jasig.portal.security.IPerson#setAttributes(java.util.Map)
     */
    public void setAttributes(final Map<String, List<Object>> attrs) {
        for (final Entry<String, List<Object>> attrEntry : attrs.entrySet()) {
            final String key = attrEntry.getKey();
            final List<Object> value = attrEntry.getValue();
            this.setAttribute(key, value);
        }
    }

    /**
     * Returns the user's ID that was used for authentication.
     * Does not correlate to any eduPerson attribute but is the key
     * for user data in uPortal reference rdbms.
     * @return User's ID.
     */
    public int getID() {
        return m_ID;
    }

    /**
     * Sets the user's ID.
     * @param sID User's ID as supplied for authentication
     * Does not correlate to any eduPerson attribute but is the key
     * for user data in uPortal reference rdbms.
     */
    public void setID(int sID) {
        m_ID = sID;
    }
    
    /* (non-Javadoc)
     * @see org.jasig.portal.security.IPerson#getUserName()
     */
    public String getUserName() {
        return (String)this.getAttribute(IPerson.USERNAME);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.security.IPerson#setUserName(java.lang.String)
     */
    public void setUserName(String userName) {
        this.setAttribute(IPerson.USERNAME, userName);
    }

    /**
     * Returns the user's name that was established during authentication.
     * Correlates to cn (common name) in the eduPerson 1.0 specification.
     * @return User's name.
     */
    public String getFullName() {
        return m_FullName;
    }

    /**
     * Sets the user's full name.
     * @param sFullName User's name as established during authentication
     * Correlates to cn (common name) in the eduPerson 1.0 specification.
     */
    public void setFullName(String sFullName) {
        m_FullName = sFullName;
    }

    /**
     * Determines whether or not this person is a "guest" user.
     * <p>
     * This person is a "guest" if both of the following are true:
     * <ol>
     *   <li>This person's user name matches the value of the property
     *       <code>org.jasig.portal.security.PersonImpl.guest_user_name</code>
     *       in <code>portal.properties</code>.</li>
     *   <li>This person does not have a live instance ISecurityContext that 
     *       states he/she has been successfully authenticated.  (It can be 
     *       either null or unauthenticated.)</li>
     * </ol>
     * 
     * @return <code>true</code> If person is a guest, otherwise <code>false</code>
     */
    public boolean isGuest() {
        boolean isGuest = false;  // default
        String userName = (String) getAttribute(IPerson.USERNAME);
        if (PersonFactory.GUEST_USERNAME.equalsIgnoreCase(userName) && 
                (m_securityContext == null || !m_securityContext.isAuthenticated())) {
            isGuest = true;
        }
        return isGuest;
    }

    /**
     * Returns an EntityIdentifier for this person.  The key contains the value
     * of the eudPerson username attribute, or null
     *
     * @return EntityIdentifier with attribute 'username' as key.
     */
    public EntityIdentifier getEntityIdentifier() {
        return m_eid;
    }

    /* (non-Javadoc)
    * @see java.security.Principal#getName()
    */
    public String getName() {
        return (String) getAttribute(IPerson.USERNAME);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", m_ID)
                .append("fullName", m_FullName)
                .append("attributes", userAttributes)
                .append("securityContext", m_securityContext)
                .append("isGuest", isGuest())
                .toString();
    }

    @Override
    public int hashCode() {
    	int result = new HashCodeBuilder(209348721,-93847839)
    		.append(m_ID)
    		.toHashCode();
    	return result;
    }

    @Override
    public boolean equals(Object obj) {
    	if(obj == this)
    		return true;
    	if(!(obj instanceof IPerson))
    		return false;
    	
    	IPerson other = (IPerson) obj;
    	return new EqualsBuilder()
    		.append(this.getID(), other.getID())
    		.isEquals();
    }

}
