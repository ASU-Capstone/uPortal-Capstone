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
package org.jasig.portal.groups.pags.testers;

import java.util.regex.Pattern;

import org.jasig.portal.groups.pags.dao.IPersonAttributesGroupTestDefinition;

/**
 * A tester for matching the possibly multiple values of an attribute 
 * against a regular expression.  The match function attempts to match the 
 * entire region against the pattern specified. 
 * <p>
 * For example, if the pattern is specified as "<strong><code>^02([A-D])*</code></strong>":
 * 
 * <code>
 * <table border='2' width='100%'>
 *  <tr>
 *    <td><strong>Input</strong></td><td><strong>Matches</strong></td>
 *  </tr>
 *  <tr>
 *    <td>02A</td><td>Yes</td>
 *  </tr>
 *  <tr>
 *    <td>02ABCD</td><td>Yes</td>
 *  </tr>
 *  <tr>
 *    <td>A02D</td><td>No</td>
 *  </tr>
 *  <tr>
 *    <td>02</td><td>Yes</td>
 *  </tr>
 *  <tr>
 *    <td>02MisMatch</td><td>No</td>
 *  </tr>
 *  <tr>
 *    <td>PatternWillNeverMatch</td><td>No</td>
 *  </tr>
 * </table>
 * </code>
 * @author Dan Ellentucke
 * @author Misagh Moayyed
 * @see EagerRegexTester
 */
public class RegexTester extends StringTester {
    protected Pattern pattern;

    /**
     * @since 4.3
     */
    public RegexTester(IPersonAttributesGroupTestDefinition definition) {
        super(definition);
        this.pattern = Pattern.compile(definition.getTestValue());
    }

    /**
     * @deprecated use {@link EntityPersonAttributesGroupStore}, which leverages
     * the single-argument constructor.
     */
    @Deprecated
    public RegexTester(String attribute, String test) {
        super(attribute, test);
        this.pattern = Pattern.compile(test);
    }

    /**
     * Sets the pattern string to use for the regex test.
     * @param patternString regex pattern string
     */
    protected void setPattern(String patternString) {
        pattern = Pattern.compile(patternString);
    }

    @Override
    public boolean test(String att) {
        return pattern.matcher(att).matches();
    }
}
