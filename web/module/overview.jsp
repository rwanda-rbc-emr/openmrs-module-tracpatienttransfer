<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ taglib prefix="transfertag" uri="/WEB-INF/view/module/tracpatienttransfer/taglibs/transfertag.tld" %>
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/patienttransfers.css" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/listing.css" />

<%@ include file="template/localHeader.jsp"%>

<openmrs:require privilege="View Patient exited from care" otherwise="/login.htm" redirect="/module/@MODULE_ID@/overview.form" />

<h2><spring:message code="@MODULE_ID@.overviewOnExit"/></h2>
<br/>

<div style="margin:5px auto 5px auto; width: 80%">
	
	<div style="width: 50%; float: left;">
		<div class="list_container">
			<div class="list_title"><spring:message code="@MODULE_ID@.details"/></div>
			<table class="list_data">
				<tr>
					<th class="columnHeader">${reasonForExitingCareTitle}</th>
					<th class="columnHeader"><spring:message code="@MODULE_ID@.numberOfPatient"/></th>
				</tr>
				<c:forEach items="${reasons}" var="reason" varStatus="status">
					<tr title="<spring:message code="@MODULE_ID@.view.list"/>" onclick="window.location.href='patientExitedFromCare.list?page=1&reason=${reason.answerConcept.conceptId}'" class="row <c:if test="${status.count%2==0}">even</c:if>"><td class="rowValue">${reason.answerConcept.name}</td><td class="rowValue">${transfertag:numberOfPatientExitedFromCareByReason(reason.answerConcept.conceptId)}</td></tr>
				</c:forEach>
				<tr class="list_title"><td class="rowValue"><b><spring:message code="@MODULE_ID@.total"/></b></td><td class="rowValue"><b>${transfertag:numberOfPatientExitedFromCare()}</b></td></tr>
			</table>
		</div>
	</div>
	<div style="width: 47%; float: right;">
		<div class="list_container">
			<div class="list_title"><spring:message code="@MODULE_ID@.chart"/></div>
			<div class="chartHolder">
				<center><img src="chart.htm?chart=patientExited&width=450&height=350"/></center>
			</div>
		</div>
	</div>
	<div style="clear: both"></div>
	
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>