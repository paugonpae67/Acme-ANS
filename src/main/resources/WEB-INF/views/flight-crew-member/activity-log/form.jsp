<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	
	<acme:input-textbox code="FlightCrewMember.ActivityLog.form.label.typeOfIncident" path="typeOfIncident"/>
	<acme:input-moment code="FlightCrewMember.ActivityLog.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
	<acme:input-integer code="FlightCrewMember.ActivityLog.form.label.saverityLevel" path="saverityLevel"/>
	<acme:input-textarea code="FlightCrewMember.ActivityLog.form.label.description" path="description"/>
	
		<jstl:choose>
		
		
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete') && draftMode == true}">
			<acme:submit code="member.activity-log.form.button.update" action="/flight-crew-member/activity-log/update"/>
			<acme:submit code="member.activity-log.form.button.publish" action="/flight-crew-member/activity-log/publish"/>
			<acme:submit code="member.activity-log.form.button.delete" action="/flight-crew-member/activity-log/delete"/>
			<acme:input-checkbox code="flight-crew-member.flight-assignment.form.label.public" path="confirmation"/>
			
		</jstl:when>
		
	
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="flight-crew-member.flight-assignment.form.label.confirmation" path="confirmation"/>
			<acme:submit code="flight-crew-member.activity-log.form.button.create" action="/flight-crew-member/activity-log/create?masterId=${masterId}"/>
			
		</jstl:when>
	</jstl:choose>
	
</acme:form>