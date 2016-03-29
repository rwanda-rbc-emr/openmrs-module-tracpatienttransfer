<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/scripts/jquery-1.3.2.js" />
<%@ taglib prefix="transfertag" uri="/WEB-INF/view/module/tracpatienttransfer/taglibs/transfertag.tld" %>
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/patienttransfers.css" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/listing.css" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/listingstyle.css" />

<%@ include file="template/localHeader.jsp"%>

<openmrs:require privilege="Manage search/listing on Patient Exited from care" otherwise="/login.htm" redirect="/module/@MODULE_ID@/advancedSearch.form" />

<h2><spring:message code="@MODULE_ID@.search.advanced"/></h2>
<br/>

<div>
	<%@ include file="template/statisticParameterHeader.jsp"%>
</div>

<div style="margin:5px;">
	<div id="data" style="margin: auto; width: 97%; font-size: 0.9em;">
		<div id="list_container" style="width: 99%; padding-top: 4px;">
			<div id="list_title">
				<div class="list_title_msg">${title}</div>
				<div class="list_title_bts">
					<span>
						<openmrs:hasPrivilege privilege="Export Collective Patient Data">
							<form action="advancedSearch.form?page=1&export=csv${prmtrs}" method="post" style="display: inline;">
								<input type="submit" class="list_exportBt" value="<spring:message code="@MODULE_ID@.patientlist.xprt.csv"/>" title="<spring:message code="@MODULE_ID@.patientlist.csv"/>"/>
							</form>&nbsp;&nbsp;
							<form action="advancedSearch.form?page=1&export=pdf${prmtrs}" method="post" style="display: inline;">
								<input type="submit" class="list_exportBt" value="<spring:message code="@MODULE_ID@.patientlist.xprt.pdf"/>" title="<spring:message code="@MODULE_ID@.patientlist.pdf"/>"/>
							</form>
						</openmrs:hasPrivilege>
					</span>
				</div>
				<div style="clear:both;"></div>
			</div>
			<table id="list_data">
				<tr>
					<th rowspan="2" width="10%" class="columnHeader"><spring:message code="@MODULE_ID@.general.exitwhen"/> ?</th>
					<th rowspan="2" width="3%" class="columnHeader numberCol"><spring:message code="@MODULE_ID@.general.number"/></th>
					<openmrs:hasPrivilege privilege="View Patient Names">
						<th rowspan="2" width="16%" class="columnHeader"><spring:message code="@MODULE_ID@.general.names"/></th>
					</openmrs:hasPrivilege>
					<th rowspan="2" width="3%" class="columnHeader"><spring:message code="@MODULE_ID@.search.gender"/></th>
					<th rowspan="2" width="3%" class="columnHeader"><spring:message code="@MODULE_ID@.search.age"/> (<spring:message code="@MODULE_ID@.search.years"/>)</th>
					<th colspan="2" class="columnHeader"><center><spring:message code="Patient.identifier"/></center></th>
					<th rowspan="2" width="18%" class="columnHeader"><spring:message code="@MODULE_ID@.general.reasonofexit"/></th>
					<th rowspan="2" width="15%" class="columnHeader"><spring:message code="Encounter.provider" /></th>
					<th rowspan="2" width="15%" class="columnHeader"><spring:message code="Encounter.location" /></th>
					<th rowspan="2" width="3%" class="columnHeader"><spring:message code="@MODULE_ID@.general.view"/></th>
				</tr>			
				<tr>
					<th width="7%" class="columnHeader">${transfertag:identifierTypeNameById(tracnetIdentifierTypeId)}</th>
					<th width="7%" class="columnHeader">${transfertag:identifierTypeNameById(localIdentifierTypeId)}</th>
				</tr>
				<c:if test="${empty obsList}"><tr><td colspan="10" width="100%"  style="text-align: center;"><spring:message code="@MODULE_ID@.tablelist.empty"/></td></tr></c:if>
				<c:set value="0" var="index"/>
				<c:forEach items="${obsList}" var="obs" varStatus="status">
					<tr>
						<c:choose>
						  <c:when test="${obs.obsDatetime!=currentDate}">
						   	<c:set value="${index+1}" var="index"/>
						   	<td class="rowValue" style="border-top: 1px solid cadetblue; <c:if test="${index%2!=0}">background-color: whitesmoke;</c:if>"><c:if test="${obs.obsDatetime!=currentDate}"><openmrs:formatDate date="${obs.obsDatetime}" type="medium"/><c:set value="${obs.obsDatetime}" var="currentDate"/></c:if></td>
						  </c:when>
						  <c:otherwise>
						    <td class="rowValue" <c:if test="${index%2!=0}">style="background-color: whitesmoke;"</c:if>><c:if test="${obs.obsDatetime!=currentDate}"><openmrs:formatDate date="${obs.obsDatetime}" type="medium"/><c:set value="${obs.obsDatetime}" var="currentDate"/></c:if></td>
						  </c:otherwise>
						</c:choose>
						<td class="rowValue ${status.count%2!=0?'even':''}">${((((param.page==null || param.page=='')?1:param.page)-1)*pageSize)+status.count}.</td>
						<openmrs:hasPrivilege privilege="View Patient Names">
							<td class="rowValue ${status.count%2!=0?'even':''}"><a title="<spring:message code="patientDashboard.viewDashboard"/>" href="<openmrs:contextPath/>/patientDashboard.form?patientId=${obs.person.personId}">${obs.person.personName}</a></td>
						</openmrs:hasPrivilege>
						<td class="rowValue ${status.count%2!=0?'even':''}"><img border="0"
								src="<c:if test="${obs.person.gender=='F'}"><openmrs:contextPath/>/images/female.gif</c:if><c:if test="${obs.person.gender=='M'}"><openmrs:contextPath/>/images/male.gif</c:if>" /></td>
						<td class="rowValue ${status.count%2!=0?'even':''}">${(obs.person.age<1)?'<1':obs.person.age}</td>
						<td class="rowValue ${status.count%2!=0?'even':''}">${transfertag:personIdentifierByIds(obs.person.personId,tracnetIdentifierTypeId)}</td>
						<td class="rowValue ${status.count%2!=0?'even':''}">${transfertag:personIdentifierByIds(obs.person.personId,localIdentifierTypeId)}</td>
						<td class="rowValue ${status.count%2!=0?'even':''}">
							${obs.valueCoded.name}&nbsp;
							<c:if test="${obs.valueCoded.conceptId==patientDeceasedConceptId}">(${transfertag:obsValueFromEncounterByConceptId(obs,causeOfDeathConceptId)})</c:if>
							<c:if test="${obs.valueCoded.conceptId==patientTransferredOutConceptId}">(${transfertag:obsValueFromEncounterByConceptId(obs,transferredToLocationConceptId)})</c:if>
						</td>
						<td class="rowValue ${status.count%2!=0?'even':''}">${obs.encounter.provider.personName}</td>
						<td class="rowValue ${status.count%2!=0?'even':''}">${obs.location.name}</td>
						<td class="rowValue ${status.count%2!=0?'even':''}"><a title="<spring:message code="@MODULE_ID@.general.viewrelated.obs"/>" href="<openmrs:contextPath/>/admin/observations/obs.form?obsId=${obs.obsId}"><spring:message code="@MODULE_ID@.general.sigle.obs"/></a><c:if test="${obs.encounter!=null}">&nbsp;&nbsp;&nbsp;<a title="<spring:message code="@MODULE_ID@.general.viewrelated.encounter"/>" href="<openmrs:contextPath/>/admin/encounters/encounter.form?encounterId=${obs.encounter.encounterId}"><spring:message code="@MODULE_ID@.general.sigle.encounter"/></a></c:if></td>
					</tr>
				</c:forEach>
				<c:remove var="index"/>
			</table>
		
			<div id="list_footer">
				<div class="list_footer_info">${pageInfos}</div>
				<div class="list_footer_pages">		
					<table>
						<tr>
							<c:if test="${prevPage!=-1}">
								<td width="100px" class="" style="padding:1px 2px 1px 2px; vertical-align: text-top;">
									<a href="advancedSearch.form?page=1${prmtrs}"><div class="list_pageNumber" style="text-align: center;">|&lt; <spring:message code="@MODULE_ID@.first"/></div></a>
								</td>
								<td width="100px" class="" style="padding:1px 2px 1px 2px; vertical-align: text-top;"><a href="advancedSearch.form?page=${prevPage}${prmtrs}">
									<div class="list_pageNumber" style="text-align: center;">&lt;&lt; <spring:message code="general.previous"/></div></a>
								</td>
							</c:if>
							<c:if test="${nextPage!=-1}">
								<td width="100px" class="" style="padding:1px 2px 1px 2px; vertical-align: text-top;">
									<a href="advancedSearch.form?page=${nextPage}${prmtrs}"><div class="list_pageNumber" style="text-align: center;"><spring:message code="general.next"/> &gt;&gt;</div></a>
								</td>
								<td width="100px" class="" style="padding:1px 2px 1px 2px; vertical-align: text-top;">
									<a href="advancedSearch.form?page=${lastPage}${prmtrs}"><div class="list_pageNumber" style="text-align: center;"><spring:message code="@MODULE_ID@.last"/> &gt;|</div></a>
								</td>
							</c:if>
						</tr>
					</table>		
				</div>
				<div style="clear: both"></div>
			</div>
		</div>
			
	</div>

</div>

<script type="text/javascript">

	$(document).ready(function(){
		$("#bt_submit_id").click(function(){
			$("#page_id").val("1");
		});
	});

</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>