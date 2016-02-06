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
package org.jasig.portal.redirect;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.portlet.om.IPortletWindow;
import org.jasig.portal.portlet.registry.IPortletWindowRegistry;
import org.jasig.portal.url.IPortalUrlBuilder;
import org.jasig.portal.url.IPortalUrlProvider;
import org.jasig.portal.url.IPortletUrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

/**
 * PortletRedirectionController issues a 302 redirect from an abstract service
 * URL to the configured target URL.  This controller can be used to map
 * links from one portlet to another without requiring the linking portlet know
 * the entire URL structure of the target. 
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Controller
public class PortletRedirectionController {
    
    protected final Log log = LogFactory.getLog(getClass());

    private IPortalUrlProvider portalUrlProvider;

    @Autowired
    public void setPortalUrlProvider(IPortalUrlProvider urlProvider) {
        this.portalUrlProvider = urlProvider;
    }

    private IPortletWindowRegistry portletWindowRegistry;
    
    @Autowired
    public void setPortletWindowRegistry(IPortletWindowRegistry portletWindowRegistry) {
        this.portletWindowRegistry = portletWindowRegistry;
    }
    
    private Map<String, IRedirectionUrl> services;
    
    @Required
    @Resource(name="redirectionServices")
    public void setServices(Map<String, IRedirectionUrl> services) {
        this.services = services;
    }

    @RequestMapping(value={"{serviceKey}/*/**","{serviceKey}"})
    public void redirectExtra(HttpServletRequest request, HttpServletResponse response, @PathVariable String serviceKey) throws IOException {
        
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
/*
        String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        AntPathMatcher apm = new AntPathMatcher();
        String extraPath = apm.extractPathWithinPattern(bestMatchPattern, path);
*/
        List<String> pathElements = Arrays.asList(path.split("/"));

        final IRedirectionUrl url = services.get(serviceKey);
        if (url == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String redirectUrl = getUrlString(url, request, pathElements);

        // send a redirect
        response.sendRedirect(redirectUrl);

    }

    protected String getUrlString(IRedirectionUrl url, HttpServletRequest request, List<String> extraPath) {
        
        if (url instanceof ExternalRedirectionUrl) {
            ExternalRedirectionUrl externalUrl = (ExternalRedirectionUrl) url;
            StringBuffer urlStr = new StringBuffer();
            urlStr.append(externalUrl.getUrl());
            
            try {
            
                // add any additional parameters
                String separator = "?";
                for (Map.Entry<String, String[]> param : externalUrl.getAdditionalParameters().entrySet()) {
                    for (String value : param.getValue()) {
                        urlStr.append(separator);
                        urlStr.append(param.getKey());
                        urlStr.append("=");
                        urlStr.append(URLEncoder.encode(value, "UTF-8"));
                        separator = "&";
                    }
                }
                
                // add any dynamic parameters
                for (Map.Entry<String, String> param : externalUrl.getDynamicParameters().entrySet()) {
                    String[] values = request.getParameterValues(param.getKey());
                    if (values != null) {
                        for (String value : values) {
                            urlStr.append(separator);
                            urlStr.append(param.getValue());
                            urlStr.append("=");
                            urlStr.append(URLEncoder.encode(value, "UTF-8"));
                            separator = "&";
                        }
                    }
                }
                
                if (!extraPath.isEmpty()) {
                    
                    List<String> paramNames = externalUrl.getPathParameters();
                    
                    ListIterator<String> itt = paramNames.listIterator();
                    while(itt.hasNext() && !extraPath.isEmpty()) {
                        int index = itt.nextIndex();
                        
                        String param = itt.next();
                        String value;
                        if (itt.hasNext()){
                            value = extraPath.remove(0);
                        } else {
                            value = StringUtils.join(extraPath, "/");
                        }
                        urlStr.append(separator);
                        urlStr.append(param);
                        urlStr.append("=");
                        urlStr.append(URLEncoder.encode(value, "UTF-8"));
                        separator = "&";
                    }
                }

                return urlStr.toString();
                
            } catch (UnsupportedEncodingException ex){
                log.error("Unable to encode URL parameter for external service redirect", ex);
                return null;
            }

        } 
        
        else {
            
            PortletRedirectionUrl portletUrl = (PortletRedirectionUrl) url;
            
            // create the base URL for the portlet
            final IPortletWindow portletWindow = this.portletWindowRegistry.getOrCreateDefaultPortletWindowByFname(request, portletUrl.getFname());
            final IPortalUrlBuilder portalUrlBuilder = this.portalUrlProvider.getPortalUrlBuilderByPortletWindow(request, portletWindow.getPortletWindowId(), portletUrl.getType());
            final IPortletUrlBuilder portletUrlBuilder = portalUrlBuilder.getTargetedPortletUrlBuilder();
            portletUrlBuilder.setPortletMode(portletUrl.getMode());
            portletUrlBuilder.setWindowState(WindowState.MAXIMIZED);
            
            // for each of the defined additional parameters, add a matching
            // parameter to the portlet URL
            for (Map.Entry<String, String[]> param : portletUrl.getAdditionalParameters().entrySet()) {
                portletUrlBuilder.addParameter(param.getKey(), param.getValue());
            }
            
            // for each of the defined dynamic parameters, add a parameter if
            // the value submitted to this service was non-null
            for (Map.Entry<String, String> param : portletUrl.getDynamicParameters().entrySet()) {
                String[] values = request.getParameterValues(param.getKey());
                if (values != null) {
                    portletUrlBuilder.addParameter(param.getValue(), values);
                }
            }
            
            if (!extraPath.isEmpty()) {
                    List<String> paramNames = portletUrl.getPathParameters();
                    
                    ListIterator<String> itt = paramNames.listIterator();
                    while(itt.hasNext() && !extraPath.isEmpty()) {
                     
                        String param = itt.next();
                        String value;
                        if (itt.hasNext()){
                            value = extraPath.remove(0);
                        } else {
                            value = StringUtils.join(extraPath, "/");
                        }
                        
                        if (StringUtils.isEmpty(value)) {
                            break;
                        } else
                            portletUrlBuilder.addParameter(param, value);
                    }
                }
    
            return portalUrlBuilder.getUrlString();
        }
    }
    
}
