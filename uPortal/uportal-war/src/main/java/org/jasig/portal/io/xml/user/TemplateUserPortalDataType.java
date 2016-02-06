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
package org.jasig.portal.io.xml.user;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.jasig.portal.io.xml.AbstractPortalDataType;
import org.jasig.portal.io.xml.PortalDataKey;


/**
 * Describes a User data type in the portal
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class TemplateUserPortalDataType extends AbstractPortalDataType {
    public static final QName TEMPLATE_USER_QNAME = new QName(
            "https://source.jasig.org/schemas/uportal/io/user", 
            "template-user");
    
    /**
     * @deprecated used for importing old data files
     */
    @Deprecated
    public static final QName LEGACY_TEMPLATE_USER_QNAME = new QName("template-user");
    
    public static final PortalDataKey IMPORT_40_DATA_KEY = new PortalDataKey(
            TEMPLATE_USER_QNAME, 
            null,
            "4.0");
    
    /**
     * @deprecated used for importing old data files
     */
    @Deprecated
    public static final PortalDataKey IMPORT_32_DATA_KEY = new PortalDataKey(
            LEGACY_TEMPLATE_USER_QNAME, 
            "classpath://org/jasig/portal/io/import-user_v3-2.crn",
            null);
    
    /**
     * @deprecated used for importing old data files
     */
    @Deprecated
    public static final PortalDataKey IMPORT_30_DATA_KEY = new PortalDataKey(
            LEGACY_TEMPLATE_USER_QNAME, 
            "classpath://org/jasig/portal/io/import-user_v3-0.crn",
            null);
    
    /**
     * @deprecated used for importing old data files
     */
    @Deprecated
    public static final PortalDataKey IMPORT_26_DATA_KEY = new PortalDataKey(
            LEGACY_TEMPLATE_USER_QNAME, 
            "classpath://org/jasig/portal/io/import-user_v2-6.crn",
            null);
    
    private static final List<PortalDataKey> PORTAL_DATA_KEYS = Arrays.asList(
            IMPORT_26_DATA_KEY, IMPORT_30_DATA_KEY, IMPORT_32_DATA_KEY, IMPORT_40_DATA_KEY);
    
    public TemplateUserPortalDataType() {
        super(TEMPLATE_USER_QNAME);
    }
    
    @Override
    public List<PortalDataKey> getDataKeyImportOrder() {
        return PORTAL_DATA_KEYS;
    }

    @Override
    public String getTitleCode() {
        return "Template User";
    }

    @Override
    public String getDescriptionCode() {
        return "Portal Template Users";
    }
}
