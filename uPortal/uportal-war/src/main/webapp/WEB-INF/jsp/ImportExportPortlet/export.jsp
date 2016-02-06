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

<!-- Portlet -->
<div class="fl-widget portlet imp-exp view-export" role="section">
    
    <!-- Portlet Titlebar -->
    <div class="fl-widget-titlebar titlebar portlet-titlebar" role="sectionhead">
        <h2 class="title" role="heading"><spring:message code="export.portlet.entities"/></h2>
        <div class="toolbar" role="toolbar">
            <ul class="btn-group">
                <li class="btn"><a class="button btn btn-primary" href="<portlet:renderURL/>"><spring:message code="import"/> <i class="fa fa-upload"></i></a></li>
                <li class="btn"><a class="button btn btn-default" href="<portlet:renderURL><portlet:param name="action" value="delete"/></portlet:renderURL>"><spring:message code="delete"/> <i class="fa fa-trash-o"></i></a></li>
            </ul>
        </div>
    </div>
    
    <!-- Portlet Content -->
    <div class="fl-widget-content content portlet-content" role="main">
        
        <!-- Note -->
        <div class="portlet-note" role="note">
            <p>Select an entity to export. You can allow/disallow entity types using Portlet Preferences.  See uPortal's portlet.xml file for details.</p>
        </div>
        
        <div class="portlet-form">
            <form id="${n}form" method="POST" class="form-inline" action="javascript:;">
                <div class="form-group">
                    <label class="portlet-form-label" for="${n}entityType"><spring:message code="type"/>:</label>
                    <select id="${n}entityType" class="form-control" name="entityType">
                        <option>[<spring:message code="select.type"/>]</option>
                        <c:forEach items="${supportedTypes}" var="type">
                            <option value="${fn:escapeXml(type.typeId)}"><spring:message code="${type.titleCode}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label class="portlet-form-label" for="${n}sysid"><spring:message code="id"/>:</label>
                    <input type="text" id="${n}sysid" class="form-control" name="sysid"/>
                </div>
                <div class="form-group">
                    <div class="buttons">
                        <a id="${n}exportLink" class="button btn btn-primary" target="_blank" href=""><spring:message code="export"/> <i class="fa fa-download"></i></a>
                    </div>
                </div>
            </form>
        </div>
        
    </div> <!-- end: portlet-content -->
</div> <!-- end: portlet -->

<script type="text/javascript">
    up.jQuery(document).ready(function () {
        var $ = up.jQuery;
        
        var updateLink = function () {
            var entityType, sysId, url;
            
            entityType = $("#${n}entityType").val();
            sysId = $("#${n}sysid").val();
            
            $("#${n}exportLink").attr("href", "<c:url value="/api/entity/"/>" + entityType + "/" + sysId + "?download=true");
        };
        
        $("#${n}entityType").change(updateLink);
        $("#${n}sysid").change(updateLink);
        
    });
</script>
