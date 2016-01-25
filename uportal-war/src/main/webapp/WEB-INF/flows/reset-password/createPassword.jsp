<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:actionURL var="formUrl">
    <portlet:param name="execution" value="${flowExecutionKey}" />
</portlet:actionURL>
<c:set var="n"><portlet:namespace/></c:set>

<!-- Portlet -->
<div class="fl-widget portlet" role="section">

    <!-- Portlet Titlebar -->
    <div class="fl-widget-titlebar titlebar portlet-titlebar" role="sectionhead" data-role="header">
        <h2 class="title" role="heading"><spring:message code="set.new.account.password"/></h2>
    </div> <!-- end: portlet-titlebar -->

    <!-- Portlet Body -->
    <div class="fl-widget-content content portlet-content" role="main" data-role="content">

        <form:form modelAttribute="accountForm" action="${formUrl}" method="POST">

            <!-- Portlet Messages -->
            <spring:hasBindErrors name="form">
                <div class="portlet-msg-error portlet-msg error" role="alert">
                    <form:errors path="*" element="div"/>
                </div> <!-- end: portlet-msg -->
            </spring:hasBindErrors>
            <c:if test='${flowRequestContext.flowScope.contains("createPasswordError")}'>
                <div class="portlet-msg-error portlet-msg error" role="alert">
                    <div><spring:message code="update.password.failed"/></div>
                </div>
            </c:if>

            <!-- Portlet Section -->
            <div class="portlet-section" role="region">
                <div class="titlebar">
                    <h3 class="title" role="heading"><spring:message code="password"/></h3>
                </div>
                <div class="content">
                    <table class="portlet-table table table-hover">
                        <tbody>

                            <!--  Password and confirm password -->
                            <tr>
                                <td class="attribute-name"><strong><spring:message code="new.password"/></strong></td>
                                <td><form:password path="password"/></td>
                            </tr>
                            <tr>
                                <td class="attribute-name"><strong><spring:message code="confirm.password"/></strong></td>
                                <td><form:password path="confirmPassword"/></td>
                            </tr>

                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Portlet Section -->
            <div class="portlet-section" role="region">
                <div class="content">
                    <div class="buttons utilities">
                        <input class="button btn primary" type="submit" value="<spring:message code="update.password"/>" name="_eventId_updatePassword"/>
                    </div>
                </div>
            </div>

        </form:form>
    </div>
</div>