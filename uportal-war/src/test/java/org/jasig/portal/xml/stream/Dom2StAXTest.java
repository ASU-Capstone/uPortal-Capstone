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
package org.jasig.portal.xml.stream;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.dom.DOMSource;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class Dom2StAXTest {
    private Document document;
    
    @Before
    public void setup() throws Exception {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        
        final InputStream documentStream = this.getClass().getResourceAsStream("document.xml");
        this.document = documentBuilder.parse(documentStream);
    }
    
    @Test
    public void testDom2StAXEventReader() throws Exception {
        final XMLInputFactory newFactory = XMLInputFactory.newFactory();
        final DOMSource source = new DOMSource(this.document);
        newFactory.createXMLEventReader(source);
    }
    
    @Test
    public void testDom2StAXStreamReader() throws Exception {
        final XMLInputFactory newFactory = XMLInputFactory.newFactory();
        final DOMSource source = new DOMSource(this.document);
        newFactory.createXMLStreamReader(source);
    }
}
