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
package org.jasig.portal.portlet.om;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.IBasicEntity;
import org.jasig.portal.io.xml.IPortalData;


/**
 * A portlet definition is equivalent to a published ChannelDefinition.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface IPortletDefinition extends IBasicEntity, IPortalData {
    public static final String EDITABLE_PARAM = "editable";
    public static final String CONFIGURABLE_PARAM = "configurable";
    public static final String HAS_HELP_PARAM = "hasHelp";
    public static final String HAS_ABOUT_PARAM = "hasAbout";

    /**
     * The name of the portlet parameter that if present represents an alternative
     * URL that ought to be used to "maximize" the defined portlet.
     *
     * This is useful for portlets that when maximized ought to instead be the external URL
     * or web application that they're representing in the portal.
     */
    public static final String ALT_MAX_LINK_PARAM = "alternativeMaximizedLink";
    
    /**
     * A portlet parameter that specifies a target for the flyout, eg : _blank
     */
    public static final String TARGET_PARAM = "target";
    
    /**
     * @return The unique identifier for this portlet definition.
     */
    public IPortletDefinitionId getPortletDefinitionId();
    
    /**
     * @return The List of PortletPreferences, will not be null
     */
    public List<IPortletPreference> getPortletPreferences();
    
    /**
     * @param portletPreferences The List of PortletPreferences, null clears the preferences but actually sets an empty list
     * @return true if the portlet preferences changed
     */
    public boolean setPortletPreferences(List<IPortletPreference> portletPreferences);
    
    public PortletLifecycleState getLifecycleState();
    
	public String getFName();

	public String getName();

	public String getDescription();
	
	public IPortletDescriptorKey getPortletDescriptorKey();
	
	public String getTitle();

	/**
	 * @return Default timeout in ms, -1 means no timeout.
	 */
	public int getTimeout();
	/**
	 * @return Optional timeout for action requests in ms, if null {@link #getTimeout()} should be used, -1 means no timeout.
	 */
	public Integer getActionTimeout();
    /**
     * @return Optional timeout for event requests in ms, if null {@link #getTimeout()} should be used, -1 means no timeout.
     */
	public Integer getEventTimeout();
    /**
     * @return Optional timeout for render requests in ms, if null {@link #getTimeout()} should be used, -1 means no timeout.
     */
	public Integer getRenderTimeout();
    /**
     * @return Optional timeout for resource requests in ms, if null {@link #getTimeout()} should be used, -1 means no timeout.
     */
	public Integer getResourceTimeout();

	public IPortletType getType();

	public int getPublisherId();

	public int getApproverId();

	public Date getPublishDate();

	public Date getApprovalDate();
	
	public int getExpirerId();
	
	public Date getExpirationDate();

	/**
	 * @return a READ-ONLY copy of the parameters
	 */
	public Set<IPortletDefinitionParameter> getParameters();

	public IPortletDefinitionParameter getParameter(String key);

	public Map<String, IPortletDefinitionParameter> getParametersAsUnmodifiableMap();

	// I18n
	public String getName(String locale);

	public String getDescription(String locale);

	public String getTitle(String locale);

    /**
     * Returns the alternative maximized link (URL) associated with this portlet definition,
     * or null if none.
     *
     * Syntactic sugar for parsing potential alternative maximized link as a preferable alternative
     * to directly parsing the portlet parameters elsewhere.
     *
     * @return String representing alternative max URL, or null if none.
     *
     * @since uPortal 4.2
     */
    public String getAlternativeMaximizedLink();
    
    /**
     * Syntactic sugar for getting the target parameter from the portlet parameters.
     * @return the target tab/window
     */
    public String getTarget();

	// Setter methods
	public void setFName(String fname);

	public void setName(String name);

	public void setDescription(String descr);

	public void setTitle(String title);

	/**
	 * @param timeout The default timeout value in ms, -1 means no timeout.
	 */
	public void setTimeout(int timeout);

    /**
     * @param actionTimeout Optional timeout for action requests in ms, if null {@link #getTimeout()} will be used, -1 means no timeout.
     */
    public void setActionTimeout(Integer actionTimeout);
    /**
     * @param eventTimeout Optional timeout for event requests in ms, if null {@link #getTimeout()} will be used, -1 means no timeout.
     */
    public void setEventTimeout(Integer eventTimeout);
    /**
     * @param renderTimeout Optional timeout for render requests in ms, if null {@link #getTimeout()} will be used, -1 means no timeout.
     */
    public void setRenderTimeout(Integer renderTimeout);
    /**
     * @param resourceTimeout Optional timeout for resource requests in ms, if null {@link #getTimeout()} will be used, -1 means no timeout.
     */
    public void setResourceTimeout(Integer resourceTimeout);

	public void setType(IPortletType channelType);

	public void setPublisherId(int publisherId);

	public void setApproverId(int approvalId);

	public void setPublishDate(Date publishDate);

	public void setApprovalDate(Date approvalDate);

	public void setExpirerId(int expirerId);
	
	public void setExpirationDate(Date expirationDate);

	public void setParameters(Set<IPortletDefinitionParameter> parameters);

	public void addLocalizedTitle(String locale, String chanTitle);

	public void addLocalizedName(String locale, String chanName);

	public void addLocalizedDescription(String locale, String chanDesc);
	
	/**
	 * @return a portlet rating
	 */
	public Double getRating();

	/**
	 * @param rating sets portlet rating
	 */
	public void setRating(Double rating);

	/**
	 * @return Number of users that rated this portlet
	 */
	public Long getUsersRated();

	/**
	 * @param usersRated sets number of users that rated this portlet
	 */
	public void setUsersRated(Long usersRated);
	
	/**
	 * Implementation required by IBasicEntity interface.
	 * 
	 * @return EntityIdentifier
	 */
	@Override
    public EntityIdentifier getEntityIdentifier();

	/**
	 * Adds a parameter to this channel definition
	 * 
	 * @param parameter
	 *            the channel parameter to add
	 */
	public void addParameter(IPortletDefinitionParameter parameter);

	public void addParameter(String name, String value);

	/**
	 * Removes a parameter from this channel definition
	 * 
	 * @param parameter
	 *            the channel parameter to remove
	 */
	public void removeParameter(IPortletDefinitionParameter parameter);

	/**
	 * Removes a parameter from this channel definition
	 * 
	 * @param name
	 *            the parameter name
	 */
	public void removeParameter(String name);

	/**
	 * @return Hash code based only on the fname of the portlet definition
	 */
	@Override
	public int hashCode();
	
	/**
	 * Equals must be able to compare against any other {@link IPortletDefinition} and
	 * the comparison must only use the fname
	 */
	@Override
	public boolean equals(Object o);
}
