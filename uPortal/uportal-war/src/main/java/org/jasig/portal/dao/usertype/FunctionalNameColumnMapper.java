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
package org.jasig.portal.dao.usertype;

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class FunctionalNameColumnMapper extends AbstractStringColumnMapper<String> {
    private static final long serialVersionUID = 1L;
    
    public static final char NOT_NULL_PREFIX = '_';

    @Override
    public String fromNonNullValue(String s) {
        if (!FunctionalNameType.isValid(s)) {
            throw new IllegalArgumentException("Value from database '" + s + "' does not validate against pattern: " + FunctionalNameType.VALID_FNAME_PATTERN.pattern());
        }

        return s;
    }

    @Override
    public String toNonNullValue(String value) {
        if (!FunctionalNameType.isValid(value)) {
            throw new IllegalArgumentException("Value being stored '" + value + "' does not validate against pattern: " + FunctionalNameType.VALID_FNAME_PATTERN.pattern());
        }
        
        return value;
    }
}