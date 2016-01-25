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
package org.jasig.portal.portlets.directory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.portlet.container.properties.ThemeNameRequestPropertiesManager;
import org.jasig.portal.portlets.lookup.PersonLookupHelperImpl;
import org.jasig.portal.portlets.search.DirectoryAttributeType;
import org.jasig.portal.search.PortletUrl;
import org.jasig.portal.search.PortletUrlParameter;
import org.jasig.portal.search.PortletUrlType;
import org.jasig.portal.search.SearchConstants;
import org.jasig.portal.search.SearchRequest;
import org.jasig.portal.search.SearchResult;
import org.jasig.portal.search.SearchResults;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.IPersonManager;
import org.jasig.portal.url.IPortalRequestUtils;
import org.jasig.services.persondir.IPersonAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping("VIEW")
public class DirectoryPortletController {
    
    private static final String MAXIMIZE_ON_SEARCH_PREFERENCE = "DirectoryPortletController.maximizeOnSearch";

    protected final Log log = LogFactory.getLog(getClass());

    private IPortalRequestUtils portalRequestUtils;

    @Value("${org.jasig.portal.portlets.directory.search.result.type:Directory}")
    private String directorySearchResultType = "Directory";
    
    @Autowired(required = true)
    public void setPortalRequestUtils(IPortalRequestUtils portalRequestUtils) {
        this.portalRequestUtils = portalRequestUtils;
    }
    
    private IPersonManager personManager;
    
    @Autowired(required = true)
    public void setPersonManager(IPersonManager personManager) {
        this.personManager = personManager;
    }

    private PersonLookupHelperImpl lookupHelper;
    
    @Autowired(required = true)
    public void setPersonLookupHelper(PersonLookupHelperImpl lookupHelper) {
        this.lookupHelper = lookupHelper;
    }

    private Map<String, DirectoryAttributeType> displayAttributes;

    @Resource(name="directoryDisplayAttributes")
    public void setDirectoryDisplayAttributes(Map<String, DirectoryAttributeType> attributes) {
        this.displayAttributes = attributes;
    }

    private List<String> directoryQueryAttributes;

    @Resource(name="directoryQueryAttributes")
    public void setDirectoryQueryAttributes(List<String> attributes) {
        this.directoryQueryAttributes = attributes;
    }

    @EventMapping(SearchConstants.SEARCH_REQUEST_QNAME_STRING)
    public void search2(EventRequest request, EventResponse response) {
        
        // get the search query object from the event
        Event event = request.getEvent();
        SearchRequest query = (SearchRequest) event.getValue();

        // search the portal's directory service for people matching the request
        final List<IPersonAttributes> people = searchDirectory(query.getSearchTerms(), request);

        if (people.size() > 0) {
            // transform the list of directory results into our generic search
            // response object
            final SearchResults results = new SearchResults();
            results.setQueryId(query.getQueryId());
            results.setWindowId(request.getWindowID());
            
            for (IPersonAttributes person : people) {
                final SearchResult result = new SearchResult();
                result.setTitle((String) person.getAttributeValue("displayName"));
                result.getType().add(directorySearchResultType);
                
                PortletUrl url = new PortletUrl();
                url.setType(PortletUrlType.RENDER);
                url.setPortletMode("VIEW");
                url.setWindowState("maximized");
                PortletUrlParameter actionParam = new PortletUrlParameter();
                actionParam.setName("action");
                actionParam.getValue().add("findByUsername");
                url.getParam().add(actionParam);
                PortletUrlParameter usernameParam = new PortletUrlParameter();
                usernameParam.setName("username");
                usernameParam.getValue().add(person.getName());
                url.getParam().add(usernameParam);
                result.setPortletUrl(url);
                results.getSearchResult().add(result);
            }
            
            // fire a search response event
            response.setEvent(SearchConstants.SEARCH_RESULTS_QNAME, results);
        }
    }
    
    @RenderMapping
    public ModelAndView search(RenderRequest request,
            @RequestParam(value = "query", required = false) String query) {

        final Map<String,Object> model = new HashMap<String, Object>();

        // if the query is non-null, perform a search request
        if (query != null) {
            final List<IPersonAttributes> people = searchDirectory(query, request);
            model.put("query", query);
            model.put("people", people);
            model.put("attributeNames", this.displayAttributes);
        }

        final boolean isMobile = isMobile(request);
        String viewName = isMobile ? "/jsp/Directory/mobileDirectory" : "/jsp/Directory/directory";
        
        return new ModelAndView(viewName, model);
    }
    
    @RenderMapping(params="action=findByUsername")
    public ModelAndView findPersonByUsername(RenderRequest request, @RequestParam String username) {
        // get an authorization principal for the current requesting user
        HttpServletRequest servletRequest = portalRequestUtils.getPortletHttpRequest(request);
        IPerson currentUser = personManager.getPerson(servletRequest);

        // get the set of people matching the search query
        final IPersonAttributes person = this.lookupHelper.findPerson(currentUser, username);
        
        final boolean isMobile = isMobile(request);
        String viewName = isMobile ? "/jsp/Directory/mobileDirectory" : "/jsp/Directory/directory";

        final Map<String,Object> model = new HashMap<String, Object>();
        model.put("query", username);
        model.put("people", Collections.singletonList(person));
        model.put("attributeNames", this.displayAttributes);

        return new ModelAndView(viewName, model);
    }
    
    @ActionMapping
    public void submitSearch(ActionRequest request, ActionResponse response, 
            @RequestParam(value = "query", required = false) String query) {
        
        // Should we request to maximize?
        PortletPreferences prefs = request.getPreferences();
        boolean maximize = Boolean.parseBoolean(prefs.getValue(MAXIMIZE_ON_SEARCH_PREFERENCE, "true"));  // default is true
        if (maximize) {
            try {
                response.setWindowState(WindowState.MAXIMIZED);
            } catch (WindowStateException e) {
                log.warn("Failed to set the window state to MAXIMIZED", e);
            }
        }
        
        // Forward the query parameter...
        if (query != null) {
            response.setRenderParameter("query", query);
        }

    }
    
    @ModelAttribute("maxResults")
    public int getMaxResults() {
        return lookupHelper.getMaxResults();
    }
    
    /**
     * Search the directory for people matching the search query.  Search results
     * will be scoped to the permissions of the user performing the search.
     * 
     * @param query
     * @param request
     * @return
     */
    protected List<IPersonAttributes> searchDirectory(String query, PortletRequest request) {
        final Map<String, Object> queryAttributes = new HashMap<String, Object>();
        for (String attr : directoryQueryAttributes) {
            queryAttributes.put(attr, query);
        }

        final List<IPersonAttributes> people;

        // get an authorization principal for the current requesting user
        HttpServletRequest servletRequest = portalRequestUtils.getPortletHttpRequest(request);
        IPerson currentUser = personManager.getPerson(servletRequest);

        // get the set of people matching the search query
        people = this.lookupHelper.searchForPeople(currentUser, queryAttributes);
        return people;
    }
    
    /**
     * Determine if this should be a mobile view.
     * 
     * @param request
     * @return
     */
    protected boolean isMobile(PortletRequest request) {
        final String themeName = request.getProperty(ThemeNameRequestPropertiesManager.THEME_NAME_PROPERTY);
        return "UniversalityMobile".equals(themeName);
    }
    
}
