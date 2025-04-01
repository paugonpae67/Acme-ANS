<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list >
    <acme:list-column code="FlightCrewMember.FlightAssignment.list-past.label.payload" path="payload" width="10%"/>
    <acme:list-column code="FlightCrewMember.FlightAssignment.list-past.label.duty" path="duty" width="10%"/>
    <acme:list-column code="FlightCrewMember.FlightAssignment.list-past.label.currentStatus" path="currentStatus" width="10%"/>    
</acme:list>

<jstl:if test="${_command == 'list-future'}">
	<acme:button code="FlightCrewMember.FlightAssignment.list.button.create" action="/flight-crew-member/flight-assignment/create"/>
</jstl:if>
