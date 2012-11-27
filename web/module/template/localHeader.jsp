
<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/module/mohtracportal/tracportal.htm"><spring:message code="mohtracportal.activities"/></a>
	</li>

	<li
		<c:if test='<%= request.getRequestURI().contains("overview") %>'>class="active"</c:if>>
		<a href="overview.form"> <spring:message code="@MODULE_ID@.overviewOnExit"/> </a>
	</li>

	<openmrs:hasPrivilege privilege="Manage search/listing on Patient Exited from care">
		<li
			<c:if test='<%= request.getRequestURI().contains("patientExitedFromCareList") %>'>class="active"</c:if>>
			<a href="patientExitedFromCare.list?page=1"> <spring:message code="@MODULE_ID@.manage.list"/> </a>
		</li>
	</openmrs:hasPrivilege>

	<openmrs:hasPrivilege privilege="Manage search/listing on Patient Exited from care">
		<li
			<c:if test='<%= request.getRequestURI().contains("advancedSearch") %>'>class="active"</c:if>>
			<a href="advancedSearch.form"> <spring:message code="@MODULE_ID@.search.advanced"/> </a>
		</li>
	</openmrs:hasPrivilege>

		<li 
			<c:if test='<%= request.getRequestURI().contains("patientsResumedCareList") %>'>class="active"</c:if>>
			<a href="patientsResumedCare.list?page=1"> <spring:message code="@MODULE_ID@.patientsResumedCare"/> </a>
		</li>
	
	
</ul>
