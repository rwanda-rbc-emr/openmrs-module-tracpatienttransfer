<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/patienttransfers.css" />

<openmrs:require privilege="Resume care" otherwise="/login.htm" redirect="" />

<openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${param.patientId}" />

<h2><spring:message code="@MODULE_ID@.resumeCare" /></h2>

<br/>
<b class="boxHeader"><spring:message code="@MODULE_ID@.resumeCare"/> <c:if test="${lastObs ne null}">: ${lastObs.valueCoded.name} <spring:message code="@MODULE_ID@.general.on"/> <openmrs:formatDate date="${lastObs.obsDatetime}" type="medium"/></c:if></b>
<div class="box" id="formTransfer">
	<form method="post" action="resumeCare.form?patientId=${param.patientId}&save=true" name="resumeCareForm">
	
		<div id="errorDivNewId" style="margin-bottom: 5px;"></div>
	
		<table>
			<tr>
				<td width="250px"><input id="prtSave" value="${param.save}" type="hidden"/></td>
				<td></td>
				<td><input name="patientId" value="${patient.patientId}" type="hidden"/></td>
				<td></td>
			</tr>
			<tr>
		   		<td><spring:message code="Patient.names"/></td>
				<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.patientNames"/>"/></td>
		       	<td><b>${patient.personName}</b></td>
				<td></td>
		  	</tr>
			<tr>
		   		<td><spring:message code="@MODULE_ID@.resumeCare.reason"/></td>
				<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.resumeCare.reason"/>"/></td>
		       	<td><select name="resumeCareReason" id="resumeCareReasonId">
		       			<option value="<spring:message code="@MODULE_ID@.reason.resumeCare.error"/>"><spring:message code="@MODULE_ID@.reason.resumeCare.error"/></option>
		       			<c:if test="${lastObs.valueCoded.conceptId eq patientTransferredOutConceptId}">
		       				<option value="<spring:message code="@MODULE_ID@.reason.resumeCare.shiftingplace"/>"><spring:message code="@MODULE_ID@.reason.resumeCare.shiftingplace"/></option>
		       			</c:if>
		       			<c:if test="${lastObs.valueCoded.conceptId eq patientDefaultedConceptId}">
		       				<option selected="selected" value="<spring:message code="@MODULE_ID@.reason.resumeCare.defaulted.return"/>"><spring:message code="@MODULE_ID@.reason.resumeCare.defaulted.return"/></option>
		       			</c:if>
		       			<c:if test="${lastObs.valueCoded.conceptId eq patientRefusedConceptId}">
		       				<option selected="selected" value="<spring:message code="@MODULE_ID@.reason.resumeCare.refused.rejoin"/>"><spring:message code="@MODULE_ID@.reason.resumeCare.refused.rejoin"/></option>
		       			</c:if>
		       		</select>
		       	</td>
				<td><span id="resumeCareReasonError"></span></td>
		  	</tr> 
		  	<c:if test="${relatedEncounter ne null}">		
				<tr>
					<td></td>
			   		<td colspan="3"><div id="voidEncounterDivId" style="<c:if test="${lastObs.valueCoded.conceptId eq patientDefaultedConceptId || lastObs.valueCoded.conceptId eq patientRefusedConceptId}">display:none;</c:if>"><input type="checkbox" name="voidEncounter" id="voidEncounterId" checked="checked" disabled="disabled"/>
			   			<label for="voidEncounterId"><spring:message code="@MODULE_ID@.resumeCare.void.encounter"/>(<b>@${relatedEncounter.location} on <openmrs:formatDate date="${relatedEncounter.encounterDatetime}" type="medium"/></b>)</label>
			   				<br/>
			   				<input type="checkbox" name="unDiscontinueOrder" id="unDiscontinueOrderId" checked="checked" disabled="disabled"/>
			   			<label for="unDiscontinueOrderId"><spring:message code="@MODULE_ID@.resumeCare.unDiscontinueOrder"/></label>
			   			<br/>
			   				<input type="checkbox" name="unCompleteProgram" id="unCompleteProgramId" checked="checked" disabled="disabled"/>
			   			<label for="unCompleteProgramId"><spring:message code="@MODULE_ID@.resumeCare.unCompleteProgram"/></label>
			   			</div></td>
			  	</tr>
			</c:if> 
		  	<c:if test="${lastObs.valueCoded.conceptId eq patientDefaultedConceptId || lastObs.valueCoded.conceptId eq patientRefusedConceptId}">		
				<tr>
					<td></td>
			   		<td colspan="3">
			   			<div id="patientDefaultedBackDivId">
				   			<table>
				   				<tr>
				   					<td colspan="3">
				   						<input type="checkbox" name="enrollInHivProgram" id="enrollInHivProgramId" checked="checked" disabled="disabled"/>
			   							<label for="enrollInHivProgramId"><spring:message code="@MODULE_ID@.resumeCare.enrollInHIVProgram"/></label>
				   					</td>
				   				</tr>
				   				<tr>
				   					<td><spring:message code="@MODULE_ID@.resumeCare.enrollment.date"/></td>
				   					<td><input id="enrollmentDate" name="enrollmentDate" value="" size="11" type="text" onclick="showCalendar(this)" autocomplete="off" /></td>
				   					<td><span id="enrollmentDateError"></span></td>
				   				</tr>
				   			</table>
			   			</div>
			   		</td>
			  	</tr>
			</c:if>
		  	<tr>
		  		<td></td>
		  		<td></td>
		  		<td>
		  			<input id="btSave" type="button" onclick="fxSave();" value='<spring:message code="general.save"/>'/>
		  			<input id="btCancel" type="button" onclick="fxCancel();" value='<spring:message code="general.cancel"/>'/>
		  		</td>
				<td></td>
		  	</tr>
		</table>
	</form>
</div>

<script type="text/javascript">
	jQuery(document).ready(function(){
		
		jQuery("#resumeCareReasonId").change( function() {
			if (jQuery("#resumeCareReasonId").val()=="<spring:message code='@MODULE_ID@.reason.resumeCare.error'/>") {
				jQuery("#voidEncounterDivId").show(200);
				jQuery("#patientDefaultedBackDivId").hide(200);
	        }else {
				jQuery("#voidEncounterDivId").hide();
				if (jQuery("#resumeCareReasonId").val()=="<spring:message code='@MODULE_ID@.reason.resumeCare.defaulted.return'/>") {
					jQuery("#patientDefaultedBackDivId").show(200);
				}else jQuery("#patientDefaultedBackDivId").hide(200);
	        }
		});		
		
	});
</script>

<script type="text/javascript">

	function fxCancel(){
		self.close();
		window.opener.location.reload();
	}

	function fxSave(){
		if (validateFields()){

			var msg="<spring:message code='@MODULE_ID@.general.message.confirm.save'/>";
			
			if (confirm(msg)) {
				document.resumeCareForm.submit();
		    }
		}
	}

	function backToParent(){
		if (jQuery("#prtSave").val()=="true"){
			jQuery("#formTransfer").html("<div onclick='fxCancel()' id='savedDiv'> <spring:message code='@MODULE_ID@.general.careresumed'/> </div>");
			setTimeout(fxCancel,2000);
		}
	}
	backToParent();

	function validateFields(){
		var valid=true;

		if(jQuery("#resumeCareReasonId").val()=="<spring:message code='@MODULE_ID@.reason.resumeCare.defaulted.return'/>"){
			if(jQuery("#enrollmentDate").val()==''){
				jQuery("#enrollmentDateError").html("*");
				jQuery("#enrollmentDateError").addClass("error");
				valid=false;
			} else {
				jQuery("#enrollmentDateError").html("");
				jQuery("#enrollmentDateError").removeClass("error");
			}
		}
		
		/*if(jQuery("#reasonPatientExitedCareId").val()==jQuery("#patientDeadConceptId").val()){
			if(jQuery("#dateOfDeath").val()==''){
				jQuery("#dateOfDeathError").html("*");
				jQuery("#dateOfDeathError").addClass("error");
				valid=false;
			} else {
				jQuery("#dateOfDeathError").html("");
				jQuery("#dateOfDeathError").removeClass("error");
			}

			if(jQuery("#causeOfDeathId").val()=='0'){
				jQuery("#causeOfDeathError").html("*");
				jQuery("#causeOfDeathError").addClass("error");
				valid=false;
			} else {
				jQuery("#causeOfDeathError").html("");
				jQuery("#causeOfDeathError").removeClass("error");
			
		}}*/

		if(!valid){
			jQuery("#errorDivNewId").addClass("error");
			/*if(sameLoc){
				jQuery("#errorDivNewId").html("[ * ]  These fields are required, fill all of them before submitting."
						+"<br/>[ ** ]  Location_from and Location_to cannot be the same.");
			}else */
				jQuery("#errorDivNewId").html("[ * ]  These fields are required, fill all of them before submitting.");
		} else {
			jQuery("#errorDivNewId").html("");
			jQuery("#errorDivNewId").removeClass("error");
		}
		
		return valid;
	}

</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>