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
package org.jasig.portal.security.xslt;

/**
 * Authorization helper APIs.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface IXalanAuthorizationHelper {

    /**
     * Checks if the specified user can render the specified channel.
     * 
     * @param userName Looks up the user with the matching {@link org.jasig.portal.security.IPerson#USERNAME}
     * @param channelFName Looks up the {@link org.jasig.portal.ChannelDefinition} with the matching fname
     * @return true if the user has permission to render the channel, false for any other case.
     */
    boolean canRender(final String userName, final String channelFName);

    /**
     * Checks if the specified user can perform the specified action.  The 
     * target parameter should be non-null where applicable, otherwise it is not 
     * checked.
     *
     * @param owner The owner of the permission, e.g. <code>UP_USERS</code>
     * @param activity The behavior the user may/may not perform
     * @param target The object upon which the behavior may/may not be performed, if applicable
     * @return True if the user has permission to perform the specified action 
     * on the specified target
     */
    boolean hasPermission(String owner, String activity, String target);

}
