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
package org.jasig.portal.security.provider.cas;

import java.util.ArrayList;
import java.util.List;
import java.security.Principal;

import junit.framework.TestCase;

/**
 * A JUnit test that exercises the CasSecurityContextMock mock object.
 * The mock object is useless if it cannot be relied upon to correctly emulate
 * an ICasSecurityContext instance as configured.  This test is intended to
 * increase confidence that the mock object behaves as advertised.
 */
public class CasSecurityContextMockTest
    extends TestCase {

    /**
     * Test that the mock object properly logs targets.
     */
    public void testTargetsLog() throws CasProxyTicketAcquisitionException {

        CasSecurityContextMock mock = new CasSecurityContextMock();

        mock.getCasServiceToken("aTarget");
        mock.getCasServiceToken("anotherTarget");

        List expectedTargets = new ArrayList();
        expectedTargets.add("aTarget");
        expectedTargets.add("anotherTarget");

        assertEquals(expectedTargets, mock.getServiceTokenTargets());

    }

    /**
     * Test that the mock object returns and throws as designed.
     */
    public void testProgrammedReturns() {
        CasSecurityContextMock mock = new CasSecurityContextMock();

        List dummyProxyTickets = new ArrayList();
        dummyProxyTickets.add("token1");
        dummyProxyTickets.add("token2");

        mock.setServiceTokensToVend(dummyProxyTickets);

        try {
            assertEquals("token1", mock.getCasServiceToken("aTarget"));
            assertEquals("token2", mock.getCasServiceToken("anotherTarget"));
        } catch (CasProxyTicketAcquisitionException cptae) {
            fail("Threw where should have returned the given Strings.");
        }


        List vendedTokens = new ArrayList();
        vendedTokens.add("token1");
        vendedTokens.add("token2");

        assertEquals(vendedTokens, mock.getVendedServiceTokens());

        List dummyExceptions = new ArrayList();
        RuntimeException dummyRuntimeException = new RuntimeException();
        dummyExceptions.add(dummyRuntimeException);

        CasProxyTicketAcquisitionException cptae
            = new CasProxyTicketAcquisitionException("dummyservice", new Principal() {
            public String getName() {
                return "test";
            }
        });

        dummyExceptions.add(cptae);

        mock.setServiceTokensToVend(dummyExceptions);

        boolean exceptionThrown = false;
        try {
            mock.getCasServiceToken("yetAnotherTarget");
        }  catch (CasProxyTicketAcquisitionException e) {
            fail("Expected RuntimeException, not CasProxyTicketAcquisitionException");
        } catch (RuntimeException re) {
            assertEquals(dummyRuntimeException, re);
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            mock.getCasServiceToken("finalTarget");
        } catch (CasProxyTicketAcquisitionException e) {
            assertEquals(cptae, e);
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

        vendedTokens.addAll(dummyExceptions);

        assertEquals(vendedTokens, mock.getVendedServiceTokens());

    }

}
