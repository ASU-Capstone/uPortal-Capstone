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
package org.jasig.portal.groups.pags.dao;

import java.util.Set;

/**
 * Provides APIs for creating, storing and retrieving {@link IPersonAttributesGroupDefinition} objects.
 * 
 * @author Shawn Connolly, sconnolly@unicon.net
 */
public interface IPersonAttributesGroupDefinitionDao {

    public IPersonAttributesGroupDefinition updatePersonAttributesGroupDefinition(IPersonAttributesGroupDefinition personAttributesGroupDefinition);
    public void deletePersonAttributesGroupDefinition(IPersonAttributesGroupDefinition definition);
    /**
     * TODO:  This method probably doesn't need to return a Set.  We should refactor it to return a single object.
     */
    public Set<IPersonAttributesGroupDefinition> getPersonAttributesGroupDefinitionByName(String groupKey);
    public Set<IPersonAttributesGroupDefinition> getPersonAttributesGroupDefinitions();
    public IPersonAttributesGroupDefinition createPersonAttributesGroupDefinition(String name, String description);
    public Set<IPersonAttributesGroupDefinition> getParentPersonAttributesGroupDefinitions(IPersonAttributesGroupDefinition group);

}
