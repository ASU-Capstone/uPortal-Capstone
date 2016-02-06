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
package  org.jasig.portal.security.mvc;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.IPersonManager;
import org.jasig.portal.url.IPortalUrlBuilder;
import org.jasig.portal.url.IPortalUrlProvider;
import org.jasig.portal.url.UrlType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to work with the local login form.
 * The form presented by org.jasig.portal.channels.CLogin is typically used to generate the post to this servlet.
 * Actual login processing occurs in PortalPreAuthenticatedProcessingFilter.
 * @author Bernie Durfee (bdurfee@interactivebusiness.com)
 * @version $Revision$
 * @author Don Fracapane (df7@columbia.edu)
 */
@Controller
@RequestMapping("/Login")
public class LoginController {
    public static final String REFERER_URL_PARAM = "refUrl";

    public static final String AUTH_ATTEMPTED_KEY = "up_authenticationAttempted";
    public static final String AUTH_ERROR_KEY = "up_authenticationError";
    public static final String ATTEMPTED_USERNAME_KEY = "up_attemptedUserName";
    public static final String REQUESTED_PROFILE_KEY = "profile";

    protected final Log log = LogFactory.getLog(getClass());
    protected final Log swapperLog = LogFactory.getLog("org.jasig.portal.portlets.swapper");

    // Disallow /Login/refUrl=//location which could maliciously redirect user's browser to another site
    // by whitelisting a refUrl of nothing, /, or / plus anything other than another /
    private static final Pattern LOCAL_URL_PATTERN = Pattern.compile("|/|/[^/].*");
    private IPortalUrlProvider portalUrlProvider;
    private IPersonManager personManager;

    @Autowired
    public void setPersonManager(IPersonManager personManager) {
        this.personManager = personManager;
    }

    @Autowired
    public void setPortalUrlProvider(IPortalUrlProvider portalUrlProvider) {
        this.portalUrlProvider = portalUrlProvider;
    }

    /**
     * Process the incoming HttpServletRequest.  Note that this processing occurs after
     * PortalPreAuthenticatedProcessingFilter has run and performed pre-processing.
     * @param request
     * @param response
     * @exception ServletException
     * @exception IOException
     */
    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // create the redirect URL, adding fname and args parameters if necessary
        String redirectTarget = null;

        final String refUrl = request.getParameter(REFERER_URL_PARAM);
        if (refUrl != null) {
            if (isLocalUrl(refUrl)) {
                redirectTarget = refUrl;
            }
            else {
                log.warn("Reference URL passed in does not start with a / and will be ignored: " + refUrl);
            }
        }

        if (redirectTarget == null) {
            /* Grab the target functional name, if any, off the login request.
             * Also any arguments for the target. We will pass them  along after authentication.
             */
            String targetFname = request.getParameter("uP_fname");

            if (targetFname == null) {
                final IPortalUrlBuilder defaultUrl = this.portalUrlProvider.getDefaultUrl(request);
                redirectTarget = defaultUrl.getUrlString();
            }
            else {
                try {
                    final IPortalUrlBuilder urlBuilder = this.portalUrlProvider.getPortalUrlBuilderByPortletFName(request, targetFname, UrlType.RENDER);

                    @SuppressWarnings("unchecked")
                    Enumeration<String> e = request.getParameterNames();
                    while (e.hasMoreElements()) {
                        String paramName = e.nextElement();
                        if (!paramName.equals("uP_fname")) {
                            urlBuilder.addParameter(paramName, request.getParameterValues(paramName));
                        }
                    }

                    redirectTarget = urlBuilder.getUrlString();
                }
                catch (IllegalArgumentException e) {
                    final IPortalUrlBuilder defaultUrl = this.portalUrlProvider.getDefaultUrl(request);
                    redirectTarget = defaultUrl.getUrlString();
                }
            }
        }

        IPerson person = null;

        final Object authError = request.getSession(false).getAttribute(LoginController.AUTH_ERROR_KEY);
        if (authError == null || !((Boolean)authError)) {
            person = this.personManager.getPerson(request);
        }

        if (person == null || !person.getSecurityContext().isAuthenticated()) {
            if (request.getMethod().equals("POST"))
                request.getSession(false).setAttribute(AUTH_ATTEMPTED_KEY, "true");
            // Preserve the attempted username so it can be redisplayed to the user by CLogin
            String attemptedUserName = request.getParameter("userName");
            if (attemptedUserName != null)
                request.getSession(false).setAttribute(ATTEMPTED_USERNAME_KEY, request.getParameter("userName"));
        }

        final String encodedRedirectURL = response.encodeRedirectURL(redirectTarget);

        if (log.isDebugEnabled()) {
            log.debug("Redirecting to " + redirectTarget);
        }

        response.sendRedirect(encodedRedirectURL);
    }

    /**
     * Test if URL string is a local URL.
     *
     * @param url   URL to check
     * @return      {@code True} if it is local, else return {@code False}
     */
    static boolean isLocalUrl(final String url) {
        Matcher m = LOCAL_URL_PATTERN.matcher(url);
        return m.matches();
    }
}
