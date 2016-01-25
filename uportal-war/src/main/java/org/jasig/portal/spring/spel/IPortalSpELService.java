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
package org.jasig.portal.spring.spel;

import org.springframework.binding.expression.spel.SpringELExpressionParser;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.web.context.request.WebRequest;

/**
 * IPortalSpELService provides an interface for parsing strings using the
 * Spring expression language.  Strings are assumed to be a potential mixture
 * of SpEL expressions contained inside a ${ } and text string content.  For 
 * example, a portal-relative URL path might be formatted as 
 * ${request.contextPath}/my/path.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 * @version $Revision$
 */
public interface IPortalSpELService {
    
    /**
     * @see SpringELExpressionParser#parseExpression(String, org.springframework.binding.expression.ParserContext)
     */
    public Expression parseExpression(String expressionString) throws ParseException;

    /**
     * @see SpringELExpressionParser#parseExpression(String, org.springframework.binding.expression.ParserContext)
     */
    public Expression parseExpression(String expressionString, ParserContext context) throws ParseException;

    /**
     * Parse the supplied string by replacing any ${ } blocks with the 
     * SpEL-evaluated value.
     * 
     * @param string  string to be evaluated
     * @param request request (may be either a portlet or servlet request)
     * @return        evaluated string
     */
    public String parseString(String string, WebRequest request);
    
    /**
     * Evaluates the expression in the standard portal evaluation context
     * 
     * @see Expression#getValue(org.springframework.expression.EvaluationContext)
     */
    public Object getValue(Expression expression, WebRequest request);
    
    /**
     * @see #parseExpression(String)
     * @see #getValue(Expression, WebRequest)
     */
    public Object getValue(String expressionString, WebRequest request);
    
    /**
     * Evaluates the expression in the standard portal evaluation context
     * 
     * @see Expression#getValue(org.springframework.expression.EvaluationContext, Class)
     */
    public <T> T getValue(Expression expression, WebRequest request, Class<T> desiredResultType);
    
    /**
     * @see #parseExpression(String)
     * @see #getValue(Expression, WebRequest, Class)
     */
    public <T> T getValue(String expressionString, WebRequest request, Class<T> desiredResultType);

    /**
     * Parses an expression string against a provided SpEL environment.  The environment object should be a simple
     * class that has objects and getXXXX methods where XXXX is the name of the object in the expression.
     * For instance, to handle SpEL expressions of ${portlet.title} and ${user.name} you pass in an object
     * that has getPortlet() and getUser() methods, assuming ${} is your expression token prefix and suffix.
     * @param expressionString SpEL expression string to parse
     * @param spelEnvironment environment context object with getter methods matching SpEL expression object names
     * @return value of parsed expression
     * @since 4.2.0
     */
    public String getValue(String expressionString, Object spelEnvironment);
}
