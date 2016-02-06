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
package org.jasig.portal.security.provider;

/**
 * <p>The factory class for the simple security context. Just returns a new
 * instance of the JAASSecurityContext.</p>
 *
 * @author Nathan Jacobs
 * @version $Revision$ $Date$
 */

import org.jasig.portal.security.ISecurityContext;
import org.jasig.portal.security.ISecurityContextFactory;

public class JAASSecurityContextFactory implements ISecurityContextFactory {
  public ISecurityContext getSecurityContext() {
    return new JAASSecurityContext();
  }
}
