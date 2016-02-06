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
package org.jasig.portal.utils;

import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Provides a means to resolve uPortal DTDs
 * @author Peter Kharchenko, pkharchenko@unicon.net
 * @author Ken Weiner, kweiner@unicon.net
 * @author Dave Wallace, dwallace@udel.edu modifications
 * @version $Revision$
 */
public class DTDResolver implements EntityResolver {
    private static final String dtdPath = "/dtd";

    private static class PublicId {
        public String publicId;
        public String dtdFile;

        public PublicId(final String publicId, final String dtdFile) {
            this.publicId = publicId;
            this.dtdFile = dtdPath + "/" + dtdFile;
        }
    }

    private static final PublicId[] publicIds = new PublicId[] {
            new PublicId("-//Netscape Communications//DTD RSS 0.91//EN", "rss-0.91.dtd"),
            new PublicId("-//uPortal//Tables/EN", "tables.dtd"),
            new PublicId("-//uPortal//PersonDirs/EN", "PersonDirs.dtd"),
            new PublicId("-//uPortal//Channel Publishing/EN", "channelPublishingDocument.dtd"),
            new PublicId("-//uPortal//Data/EN", "data.dtd"),
            new PublicId("-//uPortal//PAGSGroupStore/EN", "PAGSGroupStore.dtd"),
            new PublicId("-//uPortal//LDAPGroupStore/EN", "LDAPGroupStore.dtd"),
            new PublicId("-//uPortal//Services/EN", "services.dtd"),
            new PublicId("-//W3C//DTD XHTML 1.0 Transitional//EN", "xhtml1-transitional.dtd"),
            new PublicId("-//W3C//DTD XHTML 1.0 Strict//EN", "xhtml1-strict.dtd"),
            new PublicId("-//W3C//ENTITIES Latin 1 for XHTML//EN", "xhtml-lat1.ent"),
            new PublicId("-//W3C//ENTITIES Symbols for XHTML//EN", "xhtml-symbol.ent"),
            new PublicId("-//W3C//ENTITIES Special for XHTML//EN", "xhtml-special.ent")};
    private String dtdName = null;

    /**
     * Constructor for DTDResolver
     */
    public DTDResolver() {
    }

    /**
     * Constructor for DTDResolver
     * @param dtdName the name of the dtd
     */
    public DTDResolver(String dtdName) {
        this.dtdName = dtdName;
    }

    /**
     * Sets up a new input source based on the dtd specified in the xml document
     * @param publicId the public ID
     * @param systemId the system ID
     * @return an input source based on the dtd specified in the xml document
     *               or null if we don't have a dtd that matches systemId or publicId
     */
    public InputSource resolveEntity(String publicId, String systemId) {
        InputStream inStream = null;

        // Check for a match on the systemId
        if (systemId != null) {
            if (dtdName != null && systemId.indexOf(dtdName) != -1) {
                inStream = getResourceAsStream(dtdPath + "/" + dtdName);
            }
            else if (systemId.trim().equalsIgnoreCase("http://my.netscape.com/publish/formats/rss-0.91.dtd")) {
                inStream = getResourceAsStream(dtdPath + "/rss-0.91.dtd");
            }

            if (null != inStream) {
                return new InputSource(inStream);
            }
        }

        // Check for a match on the public id
        if (publicId != null) {
            publicId = publicId.trim();
            for (int i = 0; i < publicIds.length; i++) {
                if (publicId.equalsIgnoreCase(publicIds[i].publicId)) {
                    inStream = getResourceAsStream(publicIds[i].dtdFile);
                    if (null != inStream) {
                        return new InputSource(inStream);
                    }
                    break;
                }
            }
        }

        // Return null to let the parser handle this entity
        return null;
    }

    public InputStream getResourceAsStream(String resource) {
        return DTDResolver.class.getResourceAsStream(resource);
    }
}
