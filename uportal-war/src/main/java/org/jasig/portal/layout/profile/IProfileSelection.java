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
package org.jasig.portal.layout.profile;

/**
 * Represents a profile selection, which is a username and profile fname pair.
 * @since uPortal 4.2
 */
public interface IProfileSelection {

    /**
     * Get the username of the user who may have selected a profile.
     * @return non-null String username
     */
    String getUserName();

    /**
     * Get the fname of the profile the user has selected.
     * @return non-null String fname of selected profile
     */
    String getProfileFName();

    /**
     * Set the fname of the profile the user has selected.
     * @throws IllegalArgumentException if fname is null
     */
    void setProfileFName(String fname);
}
