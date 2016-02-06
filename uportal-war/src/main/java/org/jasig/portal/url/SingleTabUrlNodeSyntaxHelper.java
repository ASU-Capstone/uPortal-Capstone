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
package org.jasig.portal.url;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.xml.xpath.XPathExpression;

import org.apache.commons.lang.StringUtils;
import org.jasig.portal.IUserPreferencesManager;
import org.jasig.portal.PortalException;
import org.jasig.portal.concurrency.caching.RequestCache;
import org.jasig.portal.dao.usertype.FunctionalNameType;
import org.jasig.portal.layout.IStylesheetUserPreferencesService;
import org.jasig.portal.layout.IStylesheetUserPreferencesService.PreferencesScope;
import org.jasig.portal.layout.IUserLayout;
import org.jasig.portal.layout.IUserLayoutManager;
import org.jasig.portal.layout.PortletTabIdResolver;
import org.jasig.portal.layout.node.IUserLayoutNodeDescription;
import org.jasig.portal.layout.om.IStylesheetDescriptor;
import org.jasig.portal.layout.om.IStylesheetParameterDescriptor;
import org.jasig.portal.portlet.om.IPortletDefinition;
import org.jasig.portal.portlet.om.IPortletEntity;
import org.jasig.portal.portlet.om.IPortletEntityId;
import org.jasig.portal.portlet.om.IPortletWindow;
import org.jasig.portal.portlet.om.IPortletWindowId;
import org.jasig.portal.portlet.registry.IPortletEntityRegistry;
import org.jasig.portal.portlet.registry.IPortletWindowRegistry;
import org.jasig.portal.user.IUserInstance;
import org.jasig.portal.user.IUserInstanceManager;
import org.jasig.portal.xml.xpath.XPathOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;

/**
 * Maps tabs and portlets to folder names and back. Handles a single set of tabs and uses tab IDs for folder names.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
@Service
public class SingleTabUrlNodeSyntaxHelper implements IUrlNodeSyntaxHelper {
    public static final String EXTERNAL_ID_ATTR = "externalId";

    private static final char PORTLET_PATH_ELEMENT_SEPERATOR = '.';
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private String defaultLayoutNodeIdExpression = "/layout/folder/folder[@type='regular' and @hidden!='true'][$defaultTab]/@ID";
//    private String tabIdExpression = "/layout/folder/folder[@ID=$nodeId or descendant::node()[@ID=$nodeId]]/@ID";
    private String defaultTabParameter = "defaultTab";
    
    private IUserInstanceManager userInstanceManager;
    private XPathOperations xpathOperations;
    private IStylesheetUserPreferencesService stylesheetUserPreferencesService;
    private IPortletWindowRegistry portletWindowRegistry;
    private IPortletEntityRegistry portletEntityRegistry;
    
    @Autowired
    public void setPortletWindowRegistry(IPortletWindowRegistry portletWindowRegistry) {
        this.portletWindowRegistry = portletWindowRegistry;
    }

    @Autowired
    public void setPortletEntityRegistry(IPortletEntityRegistry portletEntityRegistry) {
        this.portletEntityRegistry = portletEntityRegistry;
    }

    @Autowired
    public void setUserInstanceManager(IUserInstanceManager userInstanceManager) {
        this.userInstanceManager = userInstanceManager;
    }

    @Autowired
    public void setXpathOperations(XPathOperations xpathOperations) {
        this.xpathOperations = xpathOperations;
    }
    
    @Autowired
    public void setStylesheetUserPreferencesService(IStylesheetUserPreferencesService stylesheetUserPreferencesService) {
        this.stylesheetUserPreferencesService = stylesheetUserPreferencesService;
    }
    
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDefaultLayoutNodeId(HttpServletRequest httpServletRequest) {
        final IUserInstance userInstance = this.userInstanceManager.getUserInstance(httpServletRequest);
        final IUserPreferencesManager preferencesManager = userInstance.getPreferencesManager();
        final IUserLayoutManager userLayoutManager = preferencesManager.getUserLayoutManager();

        final IUserLayout userLayout = userLayoutManager.getUserLayout();
        
        //This logic is specific to tab/column layouts
        final String defaultTabIndex = this.getDefaultTabIndex(httpServletRequest);
        if (defaultTabIndex != null) {
            final String defaultTabId = this.getTabId(userLayout, defaultTabIndex);
            if (StringUtils.isNotEmpty(defaultTabId)) {
                return defaultTabId;
            }
        }
    
        this.logger.warn("Failed to find default tab id for '" + userInstance.getPerson().getUserName() + "' with default tab index " + defaultTabIndex + ". Index 1 will be tried as a fall-back.");
        
        final String firstTabId = getTabId(userLayout, "1");
        if (StringUtils.isNotEmpty(firstTabId)) {
            return firstTabId;
        }
        
        this.logger.warn("Failed to find default tab id for '" + userInstance.getPerson().getUserName() + "' with default tab index 1. The user has no tabs.");
        
        return userLayout.getRootId();
    }

    protected String getTabId(final IUserLayout userLayout, final String tabIndex) {
        return this.xpathOperations.doWithExpression(
            defaultLayoutNodeIdExpression, 
            Collections.singletonMap("defaultTab", tabIndex), 
            new Function<XPathExpression, String>() {
                @Override
                public String apply(XPathExpression xPathExpression) {
                    return userLayout.findNodeId(xPathExpression);
                }
            });
    }

    /**
     * Get the index of the default tab for the user
     */
    protected String getDefaultTabIndex(HttpServletRequest httpServletRequest) {
        final String stylesheetParameter = this.stylesheetUserPreferencesService.getStylesheetParameter(httpServletRequest, PreferencesScope.STRUCTURE, this.defaultTabParameter);
        if (stylesheetParameter != null) {
            return stylesheetParameter;
        }
        
        final IStylesheetDescriptor stylesheetDescriptor = this.stylesheetUserPreferencesService.getStylesheetDescriptor(httpServletRequest, PreferencesScope.STRUCTURE);
        final IStylesheetParameterDescriptor stylesheetParameterDescriptor = stylesheetDescriptor.getStylesheetParameterDescriptor(this.defaultTabParameter);
        if (stylesheetParameterDescriptor != null) {
            return stylesheetParameterDescriptor.getDefaultValue();
        }
        
        return null;
    }

    @RequestCache(keyMask={false, true})
    @Override
    public List<String> getFolderNamesForLayoutNode(HttpServletRequest request, String layoutNodeId) {
        /*
         * Implementation note:
         * While the API allows one or more folder names, this implementation will only ever return
         * a List with zero or one element.  It's not entirely clear that a List with zero members
         * was allowed by the interface definition, but this implementation will return an empty
         * list if the layoutNodeId cannot be found in the layout.
         */


        final IUserInstance userInstance = this.userInstanceManager.getUserInstance(request);
        final IUserPreferencesManager preferencesManager = userInstance.getPreferencesManager();
        final IUserLayoutManager userLayoutManager = preferencesManager.getUserLayoutManager();
        final IUserLayout userLayout = userLayoutManager.getUserLayout();
        
        final String tabId = userLayout.findNodeId(new PortletTabIdResolver(layoutNodeId));
        
        if (StringUtils.isEmpty(tabId)) {
            return Collections.emptyList();
        }
        
        String externalId = stylesheetUserPreferencesService.getLayoutAttribute(request, PreferencesScope.STRUCTURE, tabId, EXTERNAL_ID_ATTR);
        if (externalId != null) {
            final Map<String, String> allNodesAndValuesForAttribute = stylesheetUserPreferencesService.getAllNodesAndValuesForAttribute(request, PreferencesScope.STRUCTURE, EXTERNAL_ID_ATTR);

            boolean appendNodeId = false;
            for (final Entry<String, String> nodeAttributeEntry : allNodesAndValuesForAttribute.entrySet()) {
                final String entryNodeId = nodeAttributeEntry.getKey();
                final String entryValue = nodeAttributeEntry.getValue();
                if (!tabId.equals(entryNodeId) && externalId.equals(entryValue)) {
                    appendNodeId = true;
                    break;
                }
            }
            
            if (!FunctionalNameType.isValid(externalId)) {
                logger.warn("ExternalId {} for tab {} is not a valid fname. It will be converted for use in the URL but this results in additional overhead", externalId, tabId);
                externalId = FunctionalNameType.makeValid(externalId);
            }
            
            if (appendNodeId) {
                externalId = externalId + PORTLET_PATH_ELEMENT_SEPERATOR + layoutNodeId;
            }

            logger.trace("Tab identified by {} resolved to externalId {} " +
                    "so returning that externalId as the sole folder name for node.", layoutNodeId, externalId);
            
            return Arrays.asList(externalId);

        } else {
            logger.trace("Tab identified by {} had no externalId " +
                    "so returning just {} as the sole folder name for node {}.", layoutNodeId, tabId, layoutNodeId);
            return Arrays.asList(tabId);
        }
    }

    @Override
    public String getLayoutNodeForFolderNames(HttpServletRequest request, List<String> folderNames) {
        /*
         * Implementation note: while the API specifies a List of folderNames, this implementation
         * only ever considers the first (presumably, only) value in the list.
         */

        if (folderNames == null || folderNames.isEmpty()) {
            logger.warn("Asked to get layout node for an empty or null folderNames ({}).", folderNames);
            return null;
        }

        //Check if the folder name is compound and parse it if it is
        String folderName = folderNames.get(0);
        String layoutNodeId = null;
        final int seperatorIndex = folderName.indexOf(PORTLET_PATH_ELEMENT_SEPERATOR);
        if (seperatorIndex > 0 && seperatorIndex < folderName.length() - 1) {
            layoutNodeId = folderName.substring(seperatorIndex + 1);
            folderName = folderName.substring(0, seperatorIndex);
        }

        if (folderNames.size() > 1) {
            logger.warn("Asked to consider multiple folder names {}, " +
                    "but ignoring all but the first which has been parsed as {}.", folderNames, folderName);
        }
        
        // Search the users layout attributes for a layout node with a matching externalId value
        String firstMatchingNodeId = null;
        final Map<String, String> allNodesAndValuesForAttribute = stylesheetUserPreferencesService.getAllNodesAndValuesForAttribute(request, PreferencesScope.STRUCTURE, EXTERNAL_ID_ATTR);
        for (final Entry<String, String> entry : allNodesAndValuesForAttribute.entrySet()) {
            final String value = entry.getValue();
            //Have to test against the fname safe version of the externalId since the folderName could have already been translated
            if (value.equals(folderName) || FunctionalNameType.makeValid(value).equals(folderName)) {
                final String nodeId = entry.getKey();
                
                if (nodeId.equals(layoutNodeId)) {
                    //ExternalId matched as well as the layoutNodeId, clear the firstMatchingNodeId since we found the nodeId here

                    logger.trace("Parsed folder names {} to nodeId {}.", folderNames, nodeId);

                    return nodeId;
                }
                else if (firstMatchingNodeId == null) {
                    firstMatchingNodeId = nodeId;
                }
            }
        }
        
        //If an explicit nodeId match isn't found but at least one matching externalId was found use that match
        if (firstMatchingNodeId != null) {
            layoutNodeId = firstMatchingNodeId;
        }
        //In this case the folderName must not have been an externalId, assume it is a layout node
        else if (layoutNodeId == null) {
            layoutNodeId = folderName;
        }
        
        
        //Verify the parsed layoutNodeId matches a node in the user's layout 
        final IUserInstance userInstance = this.userInstanceManager.getUserInstance(request);
        final IUserPreferencesManager preferencesManager = userInstance.getPreferencesManager();
        final IUserLayoutManager userLayoutManager = preferencesManager.getUserLayoutManager();
        
        final IUserLayoutNodeDescription node;
        try {
            node = userLayoutManager.getNode(layoutNodeId);
        }
        catch (PortalException e) {

            logger.warn("Parsed requested folder names {} to layoutNodeId {} " +
                    "but did not match a node in the user layout.", folderNames, layoutNodeId, e);

            return null;
        }
        
        if (node == null) {

            logger.warn("Parsed requested folder names to layoutNodeId {} " +
                    "but did not match a node in the user layout.", folderNames, layoutNodeId);

            return null;
        }

        String nodeId = node.getId();

        logger.trace("Resolved node id {} for folder names {}.", nodeId, folderNames);
        return nodeId;
    }

    @RequestCache(keyMask={false, true})
    @Override
    public String getFolderNameForPortlet(HttpServletRequest request, IPortletWindowId portletWindowId) {
        final IPortletWindow portletWindow = this.portletWindowRegistry.getPortletWindow(request, portletWindowId);
        final IPortletEntity portletEntity = portletWindow.getPortletEntity();
        final IPortletDefinition portletDefinition = portletEntity.getPortletDefinition();
        
        final String fname = portletDefinition.getFName();
        final String layoutNodeId = portletEntity.getLayoutNodeId();
        
        //Build the targeted portlet string (fname + subscribeId)
        return fname + PORTLET_PATH_ELEMENT_SEPERATOR + layoutNodeId;
    }

    
	@Override
	public IPortletWindowId getPortletForFolderName(HttpServletRequest request, String targetedLayoutNodeId, String folderName) {
        //Basic parsing of the 
        final String fname;
        String subscribeId = null;
        final int seperatorIndex = folderName.indexOf(PORTLET_PATH_ELEMENT_SEPERATOR);
        if (seperatorIndex <= 0 || seperatorIndex + 1 == folderName.length()) {
        	fname = folderName;
        }
        else {
            fname = folderName.substring(0, seperatorIndex);
            subscribeId = folderName.substring(seperatorIndex + 1);
        }
        
        //If a subscribeId was provided validate that it matches up with the fname
        if (subscribeId != null) {
	        final IUserInstance userInstance = this.userInstanceManager.getUserInstance(request);
	    	final IPortletEntity portletEntity = this.portletEntityRegistry.getOrCreatePortletEntity(request, userInstance, subscribeId);
	    	if (portletEntity == null || !fname.equals(portletEntity.getPortletDefinition().getFName())) {
	    		//If no entity found or the fname doesn't match ignore the provided subscribeId by setting it to null 
	    		subscribeId = null;
	    	}
	    	else {
	    		//subscribeId matches fname, lookup the window for the entity and return the windowId
	    		final IPortletEntityId portletEntityId = portletEntity.getPortletEntityId();
	    		final IPortletWindow portletWindow = this.portletWindowRegistry.getOrCreateDefaultPortletWindow(request, portletEntityId);
	    		if (portletWindow == null) {
	    		    return null;
	    		}

	    		return portletWindow.getPortletWindowId();
	    	}
        }
        
        //No valid subscribeId, find the best match based on the fname 
        
        //If a layout node is targeted then look for a matching subscribeId under that targeted node
        if (targetedLayoutNodeId != null) {
        	final IUserInstance userInstance = this.userInstanceManager.getUserInstance(request);
        	final IUserPreferencesManager preferencesManager = userInstance.getPreferencesManager();
        	final IUserLayoutManager userLayoutManager = preferencesManager.getUserLayoutManager();
        	
        	//First look for the layout node only under the specified folder 
        	subscribeId = userLayoutManager.getSubscribeId(targetedLayoutNodeId, fname);
        }

        //Find a subscribeId based on the fname
        final IPortletWindow portletWindow;
    	if (subscribeId == null) {
    		portletWindow = this.portletWindowRegistry.getOrCreateDefaultPortletWindowByFname(request, fname);
    	}
    	else {
    		portletWindow = this.portletWindowRegistry.getOrCreateDefaultPortletWindowByLayoutNodeId(request, subscribeId);
    	}
        
        if (portletWindow == null) {
        	return null;
        }
        
        return portletWindow.getPortletWindowId();
    }

}
