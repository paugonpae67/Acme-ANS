<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	
	
	<acme:input-moment code="FlightCrewMember.FlightAssignment.form.label.moment" path="moment" readonly="true"/>
	<acme:input-select code="FlightCrewMember.FlightAssignment.form.label.currentStatus" path="currentStatus" choices="${currentStatus}"/>
	<acme:input-select code="FlightCrewMember.FlightAssignment.form.label.duty" path="duty" choices="${duty}"/>
	<acme:input-textarea code="FlightCrewMember.FlightAssignment.form.label.remarks" path="remarks"/>
	<acme:input-select code="FlightCrewMember.FlightAssignment.form.label.leg" path="leg" choices="${legs}"/>	
	<acme:input-select code="FlightCrewMember.FlightAssignment.form.label.flightCrewMember" path="flightCrewMember" choices="${flightCrewMember}"/>	
	
	
	
	<jstl:choose>
	
		<jstl:when test="${_command == 'show' && draftMode == false}">
		<acme:button code="flight-crew-member.flight-assignment.form.button.activity-log" action="/flight-crew-member/activity-log/list?masterId=${id}"/>
		</jstl:when>	 
		
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:button code="flight-crew-member.flight-assignment.form.button.activity-log" action="/flight-crew-member/activity-log/list?masterId=${id}"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
			<acme:submit code="member.activity-log.form.button.publish" action="/flight-crew-member/flight-assignment/publish"/>
			
			
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="flight-crew-member.flight-assignment.form.label.confirmation" path="confirmation"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
		</jstl:when>
	</jstl:choose>
		
	
</acme:form>