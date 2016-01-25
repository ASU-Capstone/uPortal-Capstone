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
package org.jasig.portal.io;

import org.danann.cernunnos.EntityConfig;
import org.danann.cernunnos.Formula;
import org.danann.cernunnos.Phrase;
import org.danann.cernunnos.Reagent;
import org.danann.cernunnos.ReagentType;
import org.danann.cernunnos.SimpleFormula;
import org.danann.cernunnos.SimpleReagent;
import org.danann.cernunnos.TaskRequest;
import org.danann.cernunnos.TaskResponse;
import org.dom4j.Element;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IGroupConstants;
import org.jasig.portal.portlet.om.IPortletDefinition;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.services.GroupService;

public class GetMemberServicePhrase implements Phrase {

    // Instance Members.
    private Phrase element;

    /*
     * Public API.
     */

    public static final Reagent ELEMENT = new SimpleReagent("ELEMENT", "descendant-or-self::text()",
                    ReagentType.PHRASE, Element.class, "Element whose text is a member name.");

    public Formula getFormula() {
        Reagent[] reagents = new Reagent[] {ELEMENT};
        return new SimpleFormula(GetMemberServicePhrase.class, reagents);
    }

    public void init(EntityConfig config) {

        // Instance Members.
        this.element = (Phrase) config.getValue(ELEMENT);

    }

    public Object evaluate(TaskRequest req, TaskResponse res) {
        Element e = (Element) element.evaluate(req, res);
        return getPhrase(e.getName(), e.getText());
    }

    public static String getPhrase(String name, String memberValue) {
        String rslt = null;

        // We can (and must) cut & run if the element is a <literal> or a <channel>...
        if (name.equals("literal") || name.equals("channel")) {
            return "local";
        }

        try {

            Class[] leafTypes = new Class[] {IPerson.class, IPortletDefinition.class};
            for (int i=0; i < leafTypes.length && rslt == null; i++) {
                EntityIdentifier[] eis = GroupService.searchForGroups(memberValue, IGroupConstants.IS, leafTypes[i]);
                if (eis.length == 1) {
                    // Match!
                    if (eis[0].getType() == IEntityGroup.class) {
                        IEntityGroup g = GroupService.findGroup(eis[0].getKey());
                        rslt = g.getServiceName().toString();
                    } else {
                        String msg = "The specified entity is not a group:  " + memberValue;
                        throw new RuntimeException(msg);
                    }
                    break;
                } else if (eis.length > 1) {
                    String msg = "Ambiguous member name:  " + memberValue;
                    throw new RuntimeException(msg);
                }
            }

        } catch (Throwable t) {
            String msg = "Error looking up the specified member:  " + memberValue;
            throw new RuntimeException(msg, t);
        }

        if (rslt == null) {
            String msg = "The specified member was not found:  " + memberValue;
            throw new RuntimeException(msg);
        }

        return rslt;
    }

}