<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<acme:hidden-data path="flightAssignmentId"/>
	
	<h5><acme:print code="flight.message.assignment" /></h5>
	
	<acme:input-moment code="FlightCrewMember.FlightAssignment.form.label.moment" path="moment" readonly="true"/>
	<acme:input-select code="FlightCrewMember.FlightAssignment.form.label.currentStatus" path="currentStatus" choices="${currentStatus}"/>
	<acme:input-select code="FlightCrewMember.FlightAssignment.form.label.duty" path="duty" choices="${duty}"/>
	<acme:input-textarea code="FlightCrewMember.FlightAssignment.form.label.remarks" path="remarks"/>
	<acme:input-select code="FlightCrewMember.FlightAssignment.form.label.leg" path="leg" choices="${legs}"/>	
	<acme:input-select code="FlightCrewMember.FlightAssignment.form.label.flightCrewMembers" path="flightCrewMembers" choices="${flightCrewMembers}"/>	
	
	
	
	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show')}">
		<acme:button code="flight-crew-member.flight-assignment.form.button.activity-log" action="/flight-crew-member/activity-log/list?masterId=${id}"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="member.flight-assignment.form.label.confirmation" path="confirmation"/>
			<acme:submit code="member.flight-assignment.form.button.create" action="/member/flight-assignment/create"/>
		</jstl:when>
	</jstl:choose>
		
	
</acme:form>