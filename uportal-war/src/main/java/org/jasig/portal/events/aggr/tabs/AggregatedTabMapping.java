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
package org.jasig.portal.events.aggr.tabs;

/**
 * A mapped tab name
 * 
 * @author Eric Dalquist
 */
public interface AggregatedTabMapping {
    /**
     * Returned by {@link #getFragmentName()} for personal tabs
     */
    static final String PERSONAL_TAB_FRAGMENT_NAME = "CATCH_ALL_PERSONAL_TAB_OWNER";
    /**
     * Returned by {@link #getTabName()} for personal tabs
     */
    static final String PERSONAL_TAB_NAME = "Personal Tab";
    
    /**
     * Returned by {@link #getFragmentName()} for tabs where no info could be found in the portal database
     */
    static final String MISSING_TAB_FRAGMENT_NAME = "CATCH_ALL_MISSING_TAB_OWNER";
    /**
     * Returned by {@link #getTabName()} for tabs where no info could be found in the portal database
     */
    static final String MISSING_TAB_NAME = "Missing Tab";
    
    /**
     * Returned by {@link #getFragmentName()} for tabs where no info could be found in the portal database
     */
    static final String MISSING_USER_FRAGMENT_NAME = "CATCH_ALL_MISSING_USER_OWNER";


    long getId();

    /**
     * @return Name of the fragment owner for the tab
     */
    String getFragmentName();
    
    /**
     * @return Name of the mapped tab
     */
    String getTabName();

    /**
     * @return String suitable for displaying to user
     */
    String getDisplayString();
}
