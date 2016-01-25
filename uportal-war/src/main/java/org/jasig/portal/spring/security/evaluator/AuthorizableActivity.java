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
package org.jasig.portal.spring.security.evaluator;

/**
 * AuthorizableActivity represents a uPortal owner and activity combination 
 * for use by the PortalPermissionEvaluator.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Revision$
 */
public class AuthorizableActivity {

    private String activityFname;
    private String ownerFname;

    public AuthorizableActivity() {
    }

    public AuthorizableActivity(String ownerFname, String activityFname) {
        this.ownerFname = ownerFname;
        this.activityFname = activityFname;
    }

    public String getActivityFname() {
        return activityFname;
    }

    public void setActivityFname(String activityFname) {
        this.activityFname = activityFname;
    }

    public String getOwnerFname() {
        return ownerFname;
    }

    public void setOwnerFname(String ownerFname) {
        this.ownerFname = ownerFname;
    }

}
