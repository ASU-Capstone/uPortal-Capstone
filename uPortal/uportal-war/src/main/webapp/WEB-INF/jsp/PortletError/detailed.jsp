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
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<c:set var="n"><portlet:namespace/>-${portletWindowId}-</c:set>
<div class="fl-widget portlet error view-detailed" role="section">

<div class="fl-widget-titlebar titlebar portlet-titlebar" role="sectionhead">
<p><spring:message code="errorportlet.main"/></p>
<div class="breadcrumb">
<portlet:actionURL var="userResetUrl">
<portlet:param name="failedPortletWindowId" value="${ portletWindowId.stringId}"/>
</portlet:actionURL>
<span class="breadcrumb-1"><a href="${ adminRetryUrl }"><spring:message code="errorportlet.retry"/></a></span>
<span class="breadcrumb-2"><a href="${ userResetUrl }"><spring:message code="errorportlet.reset"/> (User Facing)</a></span> 
</div> <!-- end breadcrumbs -->
</div> <!-- end section head -->

<div class="fl-widget-content fl-fix up-portlet-content-wrapper">
<ul>
<li>Portlet Window ID: ${fn:escapeXml(portletWindowId)}</li>
<li>Channel Definition Name: ${fn:escapeXml(channelDefinition.name)}</li>
<li><spring:message code="errorportlet.causemessage.admin"/>: <i>${fn:escapeXml(rootCauseMessage)}</i></li>
</ul>
<div id="${n}stacktracecontainer">
<p><button class="stacktracetoggle"><spring:message code="errorportlet.toggleshow"/></button></p>
<div class="stacktrace" >
<pre>${fn:escapeXml(stackTrace)}</pre>
</div>
</div>
</div> <!-- end content -->

</div> <!-- end portlet -->
<script type="text/javascript">
up.jQuery(function() {
    var $ = up.jQuery;

    $(document).ready(function(){
    	up.showHideToggle('#${n}stacktracecontainer', { 
        	showmessage: '<spring:message code="errorportlet.toggleshow" htmlEscape="false" javaScriptEscape="true"/>',
        	hidemessage: '<spring:message code="errorportlet.togglehide" htmlEscape="false" javaScriptEscape="true"/>'
    	});
    });

});
</script>
