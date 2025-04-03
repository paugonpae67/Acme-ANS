<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list >
    	<acme:list-column code="FlightCrewMember.ActivityLog.list.label.typeOfIncident" path="typeOfIncident" width="10%"/>
    	<acme:list-column code="FlightCrewMember.ActivityLog.list.label.registrationMoment" path="registrationMoment" width="10%"/>
    	<acme:list-column code="FlightCrewMember.ActivityLog.list.label.saverityLevel" path="saverityLevel" width="10%"/>    
</acme:list>


<jstl:if test="${_command == 'list'}">
	<acme:button code="flight-crew-member.activity-log.list.button.create" action="/flight-crew-member/activity-log/create?masterId=${masterId}"/>
</jstl:if>
