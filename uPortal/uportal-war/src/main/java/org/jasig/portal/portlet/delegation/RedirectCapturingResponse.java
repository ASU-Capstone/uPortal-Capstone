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
package org.jasig.portal.portlet.delegation;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Captures a redirect instead of passing it up the chain.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
class RedirectCapturingResponse extends HttpServletResponseWrapper {
    private String location = null;
    
    public RedirectCapturingResponse(HttpServletResponse response) {
        super(response);
    }
    
    public String getRedirectLocation() {
        return this.location;
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        this.location = location;
    }
}
