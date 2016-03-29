<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/listing.css" />
<%@ taglib prefix="transfertag" uri="/WEB-INF/view/module/tracpatienttransfer/taglibs/transfertag.tld" %>

<c:if test="${pehObjects.obsReasonExitingCare ne null}">
	<h4>Current Exit Details</h4>
	<table width="99%">
		<tr>
			<th class="columnHeader" width="7%">Date</th>
			<th class="columnHeader" width="15%">Reason For Exiting Care</th>
			<th class="columnHeader" width="65%">Comment</th>
			<th class="columnHeader" width="13%">Creator</th>
		</tr>
		<tr>
			<td class="rowValue"><openmrs:formatDate date="${pehObjects.obsReasonExitingCare.obsDatetime}" type="medium"/></td>
			<td class="rowValue"><span class="lastObsValue">${pehObjects.obsReasonExitingCare.valueCoded.name}</span></td>
			<td class="rowValue">
				${pehObjects.obsReasonExitingCare.encounter}
				<c:if test="${pehObjects.obsReasonExitingCare.valueCoded.conceptId==pehObjects.patientDeceasedConceptId}">(${transfertag:obsValueFromEncounterByConceptId(pehObjects.obsReasonExitingCare,pehObjects.causeOfDeathConceptId)})</c:if>
				<c:if test="${pehObjects.obsReasonExitingCare.valueCoded.conceptId==pehObjects.patientTransferredOutConceptId}">(${transfertag:obsValueFromEncounterByConceptId(pehObjects.obsReasonExitingCare,pehObjects.transferredToLocationConceptId)})</c:if>
			</td>
			<td class="rowValue">${pehObjects.obsReasonExitingCare.creator.personName}</td>
		</tr>
	</table>
</c:if>

<h4>Care Exit History</h4>
<table width="99%">
	<tr>
		<th class="columnHeader"></th>
		<th class="columnHeader">Date</th>
		<th class="columnHeader">Reason For Exiting Care</th>
		<th class="columnHeader">Comment</th>
		<th class="columnHeader">Creator</th>
		<th class="columnHeader">Resumed by</th>
	</tr>
</table>