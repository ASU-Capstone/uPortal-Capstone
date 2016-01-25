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
<%@ taglib prefix="editPortlet" tagdir="/WEB-INF/tags/edit-portlet" %>
<portlet:actionURL var="formUrl">
    <portlet:param name="execution" value="${flowExecutionKey}" />
</portlet:actionURL>
<c:set var="n"><portlet:namespace/></c:set>

<!-- Portlet -->
<div class="fl-widget portlet user-mgr view-reviewuser" role="section">

    <!-- Portlet Titlebar -->
    <div class="fl-widget-titlebar titlebar portlet-titlebar" role="sectionhead">
        <h2 class="title" role="heading"><spring:message code="edit.my.account"/></h2>
    </div> <!-- end: portlet-titlebar -->
    
    <!-- Portlet Body -->
    <div class="fl-widget-content content portlet-content" role="main">

        <form:form id="${n}accountForm" modelAttribute="accountForm" action="${formUrl}" method="POST">

            <!-- Portlet Messages -->
            <spring:hasBindErrors name="person">
                <div class="portlet-msg-error portlet-msg error" role="alert">
                    <form:errors path="*" element="div"/>
                </div> <!-- end: portlet-msg -->
            </spring:hasBindErrors>
        
            <!-- Portlet Section -->
            <div class="portlet-section" role="region">
                <div class="titlebar">
                    <h3 class="title" role="heading"><spring:message code="my.account.details"/></h3>
                </div>
                <div id="${n}userAttributes" class="content">
                
                    <table class="portlet-table table table-hover">
                        <tbody>

                            <!-- Print out each attribute -->
                            <c:forEach items="${ editAttributes }" var="attribute">
                                <tr>
                                    <td class="attribute-name">
                                        <strong><spring:message code="${ attribute.label }"/></strong>
                                    </td>
                                    <td>
                                          <c:set var="paramPath" value="attributes['${ attribute.name }'].value"/>
                                          <editPortlet:preferenceInput input="${ attribute.preferenceInput.value }" 
                                            path="${ paramPath }" values="${ accountForm.attributes[attribute.name].value }"/>
                                    </td>
                                </tr>
                            </c:forEach>
                            
                        </tbody>
                    </table>

                </div>
            </div>

            <!-- Portlet Section -->
            <div class="portlet-section" role="region">
                <div class="titlebar">
                    <h3 class="title" role="heading"><spring:message code="password"/></h3>
                </div>
                <div class="content">
                    <table class="portlet-table table table-hover">
                        <thead>
                            <tr>
                                <th><spring:message code="attribute.name"/></th>
                                <th><spring:message code="attribute.value"/></th>
                            </tr>
                        </thead>
                        <tbody>

                            <!--  Password and confirm password -->
                            <tr>
                                <td class="attribute-name"><strong><spring:message code="password"/></strong></td>
                                <td><form:password id="${n}password" path="password"/></td>
                            </tr>
                            <tr>
                                <td class="attribute-name"><strong><spring:message code="confirm.password"/></strong></td>
                                <td><form:password id="${n}confirmPassword" path="confirmPassword"/></td>
                            </tr>

                        </tbody>
                    </table>
                </div>
            </div>    
            
            <!-- Portlet Section -->
            <div class="portlet-section" role="region">
                <div class="content">
            
                    <div class="buttons">
                        <input class="button btn primary" type="submit" value="<spring:message code="save"/>" name="_eventId_save"/>
                        <input class="button btn" type="submit" value="<spring:message code="cancel"/>" name="_eventId_cancel"/>
                    </div>
                </div>
            </div>

        </form:form>
        
    </div>

    <div id="${n}parameterForm" style="display:none">
        <form>
            <spring:message code="attribute.name"/>: <input name="name"/>
            <input type="submit" value="<spring:message code="add"/>"/>
        </form>
    </div>    
    
</div>

<script type="text/javascript">
    up.jQuery(function() {
        var $ = up.jQuery;
        $(document).ready(function(){
            up.ParameterEditor(
                $("#${n}userAttributes"), 
                {
                    parameterBindName: 'attributes',
                    multivalued: true,
                    dialog: $("#${n}parameterForm"),
                    displayClasses: {
                        deleteItemLink: "delete-attribute-link",
                        deleteValueLink: "delete-attribute-value-link",
                        addItemLink: "add-attribute-link",
                        addValueLink: "add-attribute-value-link"
                    },
                    messages: {
                        remove: '<spring:message code="remove" htmlEscape="false" javaScriptEscape="true"/>',
                        addValue: '<spring:message code="add.value" htmlEscape="false" javaScriptEscape="true"/>'
                    }
                }
            );
            $("#${n}accountForm").submit(function(e) {
                if ($("#${n}password").get(0).textLength > 0
                       && $("#${n}password").get(0).value != $("#${n}confirmPassword").get(0).value) {
                    alert ("<spring:message code="passwords.must.match" htmlEscape="false" javaScriptEscape="true"/>");
                    return false;
                }
            });
            // Clear the password field so it does not appear the user is attempting to set their password
            $("#${n}password").get(0).value="";
        });
    });
</script>
