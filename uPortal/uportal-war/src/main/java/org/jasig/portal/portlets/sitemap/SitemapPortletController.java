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
package org.jasig.portal.portlets.sitemap;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stax.StAXSource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.jasig.portal.rendering.PipelineEventReader;
import org.jasig.portal.rendering.StAXPipelineComponent;
import org.jasig.portal.url.IPortalRequestUtils;
import org.jasig.portal.url.xml.XsltPortalUrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

/**
 * SitemapPortletController produces a sitemap view of the current user's
 * portal layout.
 *
 * Woefully, this implementation is dependent upon XSLT details of the portal rendering pipeline, which
 * auto-wire in.  In the case where necessary components fail to auto-wire in, portlet exceptions out at render time.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 * @version $Revision$
 */
@Controller
@RequestMapping("VIEW")
public class SitemapPortletController {
    
    /** Name of XSL parameter indicating whether to use tab groups or not (configured in portal.properties). */
    public static final String USE_TAB_GROUPS = "USE_TAB_GROUPS";
    
    /** Name of XSL parameter representing user's locale. */
    public static final String USER_LANG = "USER_LANG";
    
    /**
     * We must use {@link StAXPipelineComponent} over the user layout document, because layout
     * document doesn't contain required attributes for tab group functionality. Those attributes
     * must be incorporated into user's layout using this component.
     */
    private StAXPipelineComponent attributeIncorporationComponent;

    /**
     * Auto-wires in the structureAttributeIncorporationComponent when available.  Annotated as not required
     * because portlet detects and treats as a runtime (rather than component-initialization-time) error failure
     * to find dependencies.
     * @param attributeIncorporationComponent
     */
    @Autowired(required = false)
    @Qualifier("structureAttributeIncorporationComponent")
    public void setStructureAttributeIncorporationComponent(StAXPipelineComponent attributeIncorporationComponent) {
        this.attributeIncorporationComponent = attributeIncorporationComponent;
    }

    /** Required by XSL to build portal URL's. */
    private IPortalRequestUtils portalRequestUtils;
    
    /**
     * Auto-wires in the PortalRequestUtils when available.  Annotated as not required
     * because portlet detects and treats as a runtime (rather than component-initialization-time) error failure
     * to find dependencies.
     * @param portalRequestUtils the portalRequestUtils to set
     */
    @Autowired(required = false)
    public void setPortalRequestUtils(IPortalRequestUtils portalRequestUtils) {
        Validate.notNull(portalRequestUtils);
        this.portalRequestUtils = portalRequestUtils;
    }
    
    /** Required by XSL to build portal URL's. */
    private XsltPortalUrlProvider xsltPortalUrlProvider;

    /**
     * Auto-wires in the XsltPortalUrlProvider when available.  Annotated as not required
     * because portlet detects and treats as a runtime (rather than component-initialization-time) error failure
     * to find dependencies.
     * @param xsltPortalUrlProvider
     */
    @Autowired(required = false)
    public void setXsltPortalUrlProvider(XsltPortalUrlProvider xsltPortalUrlProvider) {
        this.xsltPortalUrlProvider = xsltPortalUrlProvider;
    }
    
    /**
     * Whether to use tab groups or not. The value of this attribute will be passed to XSL using
     * {@value #USE_TAB_GROUPS} as parameter name.
     */
    private boolean useTabGroups;
    
    @Value("${org.jasig.portal.layout.useTabGroups}")
    public void setUseTabGroups(boolean useTabGroups) {
        this.useTabGroups = useTabGroups;
    }

    /**
     * Display the user sitemap.
     * 
     * @param request
     * @return "sitemapView" and a supporting model
     * @throws XMLStreamException
     * @throws IllegalStateException if components required for the SiteMap portlet fail to auto-wire
     */
    @RequestMapping
    public ModelAndView displaySitemap(PortletRequest request) throws XMLStreamException {
        
        Map<String, Object> model = new HashMap<String, Object>();

        if (this.xsltPortalUrlProvider == null) {
            throw new IllegalStateException("Sitemap portlet requires a XsltPortalUrlProvider but it failed to " +
                    "auto-wire");
        }

        if (this.attributeIncorporationComponent == null) {
            throw new IllegalStateException("Sitemap portlet requires a StAXPipelineComponent with qualifier structureAttributeIncorporationComponent but it failed to auto-wire");
        }

        if (this.portalRequestUtils == null) {
            throw new IllegalStateException("Sitemap portlet requires an IPortalRequestUtils but it failed to " +
                    "auto-wire");
        }


        
        // retrieve the user layout with structure attributes applied (required in order to display tab groups)
        final HttpServletRequest httpServletRequest = this.portalRequestUtils.getPortletHttpRequest(request);
        final HttpServletResponse httpServletResponse = this.portalRequestUtils.getOriginalPortalResponse(request);
        final PipelineEventReader<XMLEventReader, XMLEvent> reader = attributeIncorporationComponent.getEventReader(httpServletRequest, httpServletResponse);
        
        // create a Source from the user's layout document
        StAXSource source = new StAXSource(reader.getEventReader());
        model.put("source", source);
        model.put(XsltPortalUrlProvider.CURRENT_REQUEST, httpServletRequest);
        model.put(XsltPortalUrlProvider.XSLT_PORTAL_URL_PROVIDER, this.xsltPortalUrlProvider);
        model.put(USE_TAB_GROUPS, useTabGroups);
        model.put(USER_LANG, ObjectUtils.toString(request.getLocale()));
        
        return new ModelAndView("sitemapView", model);
    }
}
