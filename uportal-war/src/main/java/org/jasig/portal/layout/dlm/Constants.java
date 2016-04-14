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
package org.jasig.portal.layout.dlm;


/**
 * Constants used in DLM.
 * 
 * @version $Revision$ $Date$
 * @since uPortal 2.5
 */
public class Constants {

    // define the namespace prefixe on dlm elements and attributes.
    public static final String NS = "dlm:";
    public static final String NS_DECL = "xmlns:dlm";
    public static final String NS_URI = "http://www.uportal.org/layout/dlm";

    // the following variables are to support backwards compatibility for
    // users of the DLM functionality before it was rolled into the uPortal
    // baseline code. This provides for just-in-time migration of any database
    // values containing the old namespace prefix.
    public static final String LEGACY_NS = "cp:";
    public static final String LEGACY_ATT_ORIGIN      = LEGACY_NS + "origin";

    // define the names of attributes that hold layout adjustment permissions
    // LCL refers to the local name without a namespace prefix.

    public static final String LCL_DELETE_ALLOWED     = "deleteAllowed";
    public static final String LCL_MOVE_ALLOWED       = "moveAllowed";
    public static final String LCL_EDIT_ALLOWED       = "editAllowed";
    public static final String LCL_ADD_CHILD_ALLOWED  = "addChildAllowed";

    public static final String ATT_DELETE_ALLOWED     = NS+LCL_DELETE_ALLOWED;
    public static final String ATT_MOVE_ALLOWED       = NS+LCL_MOVE_ALLOWED;
    public static final String ATT_EDIT_ALLOWED       = NS+LCL_EDIT_ALLOWED;
    public static final String ATT_ADD_CHILD_ALLOWED  = NS+LCL_ADD_CHILD_ALLOWED;

    public static final String ATT_ID                  = "ID";
    public static final String ATT_TYPE                = "type";
    public static final String ATT_CHANNEL_ID          = "chanID";
    public static final String ATT_HIDDEN              = "hidden";
    public static final String ATT_NAME                = "name";
    public static final String ATT_VALUE               = "value";
    public static final String ATT_OVERRIDE            = "override";
    public static final String CAN_OVERRIDE            = "yes";

    public static final String LCL_PLF_ID              = "plfID";
    public static final String ATT_PLF_ID              = NS+LCL_PLF_ID;
    public static final String LCL_ORIGIN              = "origin";
    public static final String ATT_ORIGIN              = NS+LCL_ORIGIN;
    public static final String ATT_PRECEDENCE          = NS+"precedence";
    public static final String ATT_FRAGMENT            = NS+"fragment";
    public static final String LCL_FRAGMENT_NAME       =    "fragmentName";
    public static final String ATT_FRAGMENT_NAME       = NS+LCL_FRAGMENT_NAME;
    public static final String LCL_IS_TEMPLATE_USER    =    "isTemplateUser";
    public static final String ATT_IS_TEMPLATE_USER    = NS+LCL_IS_TEMPLATE_USER;
    public static final String ATT_TEMPLATE_LOGIN_ID   = NS+"templateLoginID";
    public static final String ATT_DEBUG_PRINT         = NS+"debugPrint";
    public static final String ATT_TARGET              = NS+"target";
    public static final String ATT_USER_VALUE          = NS+"userValue";

    public static final String ELM_POSITION_SET        = NS+"positionSet";
    public static final String ELM_POSITION            = NS+"position";
    public static final String ELM_DELETE_SET          = NS+"deleteSet";
    public static final String ELM_DELETE              = NS+"delete";
    public static final String ELM_EDIT_SET            = NS+"editSet";
    public static final String ELM_EDIT                = NS+"edit";
    public static final String ELM_PREF                = NS+"pref";
    public static final String ELM_PARAMETER           = "parameter";
    public static final String ELM_FOLDER              = "folder";
    public static final String ELM_CHANNEL             = "channel";

    public static final String ELM_PARM_SET            = NS+"parmSet";
    public static final String ELM_PARM_EDIT           = NS+"parm";

    public static final String PLF = "RDBMDistributedLayoutStore.PLF";
    public static final String STRUCTURE_PREFS
                                    = "StructureStylesheetUserPreferences";
    public static final String THEME_PREFS = "ThemeStylesheetUserPreferences";

    public static final String ROOT_FOLDER_ID="root";
    public static final String DIRECTIVE_PREFIX = "d";
    public static final String FRAGMENT_ID_USER_PREFIX = "u";
    public static final String FRAGMENT_ID_LAYOUT_PREFIX = "l";

}

