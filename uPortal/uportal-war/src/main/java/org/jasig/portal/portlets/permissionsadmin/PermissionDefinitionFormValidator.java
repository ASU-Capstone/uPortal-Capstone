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
package org.jasig.portal.portlets.permissionsadmin;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * This class validates {@link PermissionDefinitionForm} objects through the magic of
 * Webflow:  http://docs.spring.io/spring-webflow/docs/current/reference/html/views.html#view-validation-programmatic-validator
 *
 * @author Jen Bourey, jbourey@unicon.net
 */
@Component
public class PermissionDefinitionFormValidator {

    public void validateEditPermission(PermissionDefinitionForm form, MessageContext messageContext) {

        // ensure at least one principal has been assigned
        if (form.getPermissions().isEmpty()) {
            messageContext.addMessage(new MessageBuilder().error().source("principal")
                .code("please.choose.at.least.one.principal").build());
        }

    }

}
