<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/headerMinimal.jsp"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/scripts/jquery-1.3.2.js" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/patienttransfers.css" />

<openmrs:require privilege="Exit a patient from care" otherwise="/login.htm"/>

<script type="text/javascript">
	var $j = jQuery.noConflict();	
</script>

<h2><spring:message code="@MODULE_ID@.title" /></h2>

<br/>
<b class="boxHeader"><spring:message code="@MODULE_ID@.title"/></b>
<div class="box" id="formTransfer">
	<form method="post" action="exitPatientFromCare.form?patientId=${param.patientId}&save=true" name="exitPatientFromCareForm">
		
		<div id="errorDivNewId" style="margin-bottom: 5px;"></div>
		
		<table>
			<tr>
				<td width="250px"><input id="prtSave" value="${param.save}" type="hidden"/></td>
				<td></td>
				<td><input name="patientId" value="${patient.patientId}" type="hidden"/></td>
				<td></td>
			</tr>
			<tr>
				<td><input id="patientTransferedOutConceptId" value="${patientTransferedOutConceptId}" type="hidden"/></td>
				<td></td>
				<td><input id="patientDeadConceptId" value="${patientDeadConceptId}" type="hidden"/></td>
				<td></td>
			</tr>
			<tr>
		   		<td><spring:message code="Patient.names"/></td>
				<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.patientNames"/>"/></td>
		       	<td><b>${patient.personName}</b></td>
				<td></td>
		  	</tr>
		    <tr>
		   		<td><spring:message code="Encounter.provider" /></td>
				<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.provider"/>"/></td>
		       	<td><select name="provider" id="provider">
		       			<option value="0">--</option>
		       			<c:forEach items="${providers}" var="provider">
		       				<option value="${provider.key}" <c:if test="${provider.key==providerId}">selected='selected'</c:if>>${provider.value}</option>
		       			</c:forEach>
		       		</select></td>
				<td><span id="providerError"></span></td>
		    </tr>
		    <tr>
		   		<td><spring:message code="Encounter.location" /></td>
				<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.location"/>"/></td>
		      	<td><openmrs_tag:locationField formFieldName="location" initialValue="${locationId}" /></td>
				<td><span id="locationError"></span></td>
		    </tr>
			<tr>
				<td><spring:message code="Encounter.datetime"/></td>
				<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.encounterDatetime"/>"/></td>
				<td><input id="encounterDate" name="encounterDate" value="" size="11" type="text" onclick="showCalendar(this)" onchange="CompareDates('<openmrs:datePattern />');" /><span id="encounterDateError"></td>
				<td></span></td>
			</tr>
			<tr>
		   		<td>${reasonForExitingCare}</td>
				<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.reasonPatientExitedCare"/>"/></td>
		       	<td><select name="reasonExitCare" id="reasonPatientExitedCareId">
		       			<option value="0">--</option>
		       			<c:forEach items="${exitFromCareReasons}" var="reason">
		       				<option value="${reason.key}">${reason.value}</option>
		       			</c:forEach>
		       		</select>
		       	</td>
				<td><span id="reasonPatientExitedCareError"></span></td>
		  	</tr>
		  	<tr>
		  		<td colspan="4">
		  			<div id="patientTransferedOutDiv" style="display: none;">
		  				<table>
							<tr>
						   		<td width="250px">${reasonForTransferOut}</td>
								<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.reasonForTransferOut"/>"/></td>
						       	<td><textarea name="reasonTransferOut" id="reasonTransferOut" rows="3" cols="40"></textarea></td>
								<td><span id="reasonTransferOutError"></span></td>
						  	</tr>
							<tr>
						   		<td>${transferOutDate}</td>
								<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.transferOutDate"/>"/></td>
						       	<td><input name="transferOutDate" id="transferOutDate" value="" size="11" type="text" onclick="showCalendar(this)" /></td>
								<td><span id="transferOutDateError"></span></td>
						  	</tr>
							<tr>
						   		<td>${transferOutToLocation}</td>
								<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.transferOutToLocation"/>"/></td>
						       	<td><span id="locationDropDownSpan"><openmrs_tag:locationField formFieldName="locationTo" initialValue="" /></span></td>
								<td><span id="transferOutToLocationError"></span></td>
						  	</tr>
							<tr>
						   		<td><spring:message code="@MODULE_ID@.locationNotFound"/></td>
								<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.transferOutToLocationFreeText"/>"/></td>
						       	<td><input type="checkbox" onclick="showHideLocationFreeText();" name="chkbx_locationNotFound" id="chkbx_locationNotFoundId"/><span id="locationNotFoundSpan" style="display: none;"><input size="39" type="text" name="transferToLocationText" id="transferToLocationTextId"/></span></td>
								<td><span id="transferOutToLocationTextError"></span></td>
						  	</tr>  					
		  				</table>
		  			</div>
		  		</td>
		  	</tr>
		  	<tr>
		  		<td colspan="4">
		  			<div id="patientDeadDiv" style="display: none;">
		  				<table>
							<tr>
						   		<td width="250px">Date of Death</td>
								<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.dateOfDeath"/>"/></td>
						       	<td><input name="dateOfDeath" id="dateOfDeath" value="" size="11" type="text" onclick="showCalendar(this)" /></td>
								<td><span id="dateOfDeathError"></span></td>
						  	</tr>
							<tr>
						   		<td>${causeOfDeath_title}</td>
								<td><img border="0" src="<openmrs:contextPath/>/moduleResources/@MODULE_ID@/images/help.gif" title="<spring:message code="@MODULE_ID@.help.causeOfDeath"/>"/></td>
						       	<td><select name="causeOfDeath" id="causeOfDeathId">
						       			<option value="0">--</option>
						       			<c:forEach items="${causeOfDeathOptions}" var="cause_death">
						       				<option value="${cause_death.key}">${cause_death.value}</option>
						       			</c:forEach>
						       		</select>
						       	</td>
								<td><span id="causeOfDeathError"></span></td>
						  	</tr>	  					
		  				</table>
		  			</div>
		  		</td>
		  	</tr>
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
	$j(document).ready(function(){
		
		$j("#reasonPatientExitedCareId").change( function() {
			if ($j("#reasonPatientExitedCareId").val()==$j("#patientTransferedOutConceptId").val()) {
				$j("#patientTransferedOutDiv").show(500);
				$j("#patientDeadDiv").hide();
	        }else if ($j("#reasonPatientExitedCareId").val()==$j("#patientDeadConceptId").val()) {
				$j("#patientDeadDiv").show(500);
				$j("#patientTransferedOutDiv").hide();
	        }else {
				$j("#patientTransferedOutDiv").hide();
				$j("#patientDeadDiv").hide();		        
		    }
		});		
		
	});
</script>

<script type="text/javascript">

	function showHideLocationFreeText(){
		if(document.getElementById("chkbx_locationNotFoundId").checked==true){
			$j("#locationNotFoundSpan").show();
			$j("#locationDropDownSpan").hide();
		} else {
			$j("#locationNotFoundSpan").hide();
			$j("#locationDropDownSpan").show();
		}
	}

	function backToParent(){
		if ($j("#prtSave").val()=="true"){
			$j("#formTransfer").html("<div onclick='fxCancel()' id='savedDiv'> Patient Exited From Care </div>");
			setTimeout(fxCancel,2000);
		}
	}
	backToParent();

	function fxCancel(){
		self.close();
		window.opener.location.reload();
	}

	function fxSave(){
		if (validateFields()){

			var msg="You have chosen to end patient care for this patient for the following reason and on the following date, "
				+"it means also that Drug orders will be stopped and he/she will be taken out of the program"
				+"\nType of exit: "
				+$j("#reasonPatientExitedCareId :selected").text()
				+"\nDate of exit: "
				+$j("#encounterDate").val()
				+"\n\nAre you sure you want to proceed?";
			
			if (confirm(msg)) {
				document.exitPatientFromCareForm.submit();				
				//fxCancel();
		    }
		}
	}
	
	function validateFields(){
		var valid=true;
		if($j("#encounterDate").val()==''){
			$j("#encounterDateError").html("*");
			$j("#encounterDateError").addClass("error");
			valid=false;
		} else {
			$j("#encounterDateError").html("");
			$j("#encounterDateError").removeClass("error");
		}

		if(document.getElementById("provider").value=='0'){
			$j("#providerError").html("*");
			$j("#providerError").addClass("error");
			valid=false;
		} else {
			$j("#providerError").html("");
			$j("#providerError").removeClass("error");
		}

		if(document.getElementById("location").value==''){
			$j("#locationError").html("*");
			$j("#locationError").addClass("error");
			valid=false;
		} else {
			$j("#locationError").html("");
			$j("#locationError").removeClass("error");
		}

		if($j("#reasonPatientExitedCareId").val()=='0'){
			$j("#reasonPatientExitedCareError").html("*");
			$j("#reasonPatientExitedCareError").addClass("error");
			valid=false;
		} else {
			$j("#reasonPatientExitedCareError").html("");
			$j("#reasonPatientExitedCareError").removeClass("error");
		}

		if($j("#reasonPatientExitedCareId").val()==$j("#patientTransferedOutConceptId").val()){
			if($j("#transferOutDate").val()==''){
				$j("#transferOutDateError").html("*");
				$j("#transferOutDateError").addClass("error");
				valid=false;
			} else {
				$j("#transferOutDateError").html("");
				$j("#transferOutDateError").removeClass("error");
			}

			if(document.getElementById("chkbx_locationNotFoundId")!=null){
				if(document.getElementById("chkbx_locationNotFoundId").checked==true){
					if($j("#transferToLocationTextId").val()==''){
						$j("#transferOutToLocationTextError").html("*");
						$j("#transferOutToLocationTextError").addClass("error");
						valid=false;
					} else {
						$j("#transferOutToLocationTextError").html("");
						$j("#transferOutToLocationTextError").removeClass("error");
					}
				}
			}

			if($j("#locationTo").val()==''){
				if(document.getElementById("chkbx_locationNotFoundId").checked==false){
					$j("#transferOutToLocationError").html("*");
					$j("#transferOutToLocationError").addClass("error");
					valid=false;
				} else {
					$j("#transferOutToLocationError").html("");
					$j("#transferOutToLocationError").removeClass("error");
				}
			}

			var sameLoc=false;
			
			if(document.getElementById("location").value!='' && document.getElementById("locationTo").value!='' && document.getElementById("location").value==document.getElementById("locationTo").value){
				$j("#locationError").html("**");
				$j("#locationError").addClass("error");

				$j("#transferOutToLocationError").html("**");
				$j("#transferOutToLocationError").addClass("error");
				valid=false;
				sameLoc=true;
			}
		}

		if($j("#reasonPatientExitedCareId").val()==$j("#patientDeadConceptId").val()){
			if($j("#dateOfDeath").val()==''){
				$j("#dateOfDeathError").html("*");
				$j("#dateOfDeathError").addClass("error");
				valid=false;
			} else {
				$j("#dateOfDeathError").html("");
				$j("#dateOfDeathError").removeClass("error");
			}

			if($j("#causeOfDeathId").val()=='0'){
				$j("#causeOfDeathError").html("*");
				$j("#causeOfDeathError").addClass("error");
				valid=false;
			} else {
				$j("#causeOfDeathError").html("");
				$j("#causeOfDeathError").removeClass("error");
			}
		}

		if(!valid){
			$j("#errorDivNewId").addClass("error");
			if(sameLoc){
				$j("#errorDivNewId").html("[ * ]  These fields are required, fill all of them before submitting."
						+"<br/>[ ** ]  Location_from and Location_to cannot be the same.");
			}else 
				$j("#errorDivNewId").html("[ * ]  These fields are required, fill all of them before submitting.");
		} else {
			$j("#errorDivNewId").html("");
			$j("#errorDivNewId").removeClass("error");
		}
		
		return valid;
	}

	function CompareDates(dateFormat)
	{
	    var str1 = document.getElementById("encounterDate").value;
	    //var str2 = document.getElementById("nowId").value;
	    var dt1 = null;
	    var mon1 = null;
	    var yr1 = null;
	    var dt2 = null;
	    var mon2 = null;
	    var yr2 = null;
		if(dateFormat=='dd/mm/yyyy' || dateFormat=='jj/mm/aaaa') {
		    dt1  = parseInt(str1.substring(0,2),10);
		    mon1 = parseInt(str1.substring(3,5),10);
		    yr1  = parseInt(str1.substring(6,10),10);
		    //dt2  = parseInt(str2.substring(0,2),10);
		    //mon2 = parseInt(str2.substring(3,5),10);
		    //yr2  = parseInt(str2.substring(6,10),10);
		} else if(dateFormat=='mm/dd/yyyy' || dateFormat=='mm/jj/aaaa') {
		    mon1  = parseInt(str1.substring(0,2),10);
		    dt1 = parseInt(str1.substring(3,5),10);
		    yr1  = parseInt(str1.substring(6,10),10);
		    //mon2  = parseInt(str2.substring(0,2),10);
		    //dt2 = parseInt(str2.substring(3,5),10);
		    //yr2  = parseInt(str2.substring(6,10),10);
		} else{
			alert("Invalid date : "+dateFormat+": not supported !");
			$j("#encounterDate").val("");
			return;
		}
		var month1 = mon1 - 1;
	    var date1 = new Date(yr1, month1, dt1);
	    var date2 = new Date();//new Date(yr2, mon2, dt2);
	    
	    if(date2 < date1)
	    {
	    	$j("#encounterDateError").html("The date can't be in future");
	    	$j("#encounterDateError").addClass("error");
	    	$j("#encounterDate").val("");    	 
	    }
	    else
	    {
	    	$j("#encounterDateError").html("");
	    	$j("#encounterDateError").removeClass("error");
	    }
	} 

</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>
