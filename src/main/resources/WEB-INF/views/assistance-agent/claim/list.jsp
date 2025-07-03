<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistanceAgent.claim.list-finish.label.registrationMoment" path="registrationMoment" width="20%"/>
	<acme:list-column code="assistanceAgent.claim.list-finish.label.passengerEmail" path="passengerEmail" width="20%"/>
	<acme:list-column code="assistanceAgent.claim.list-finish.label.type" path="type" width="20%"/>
	<acme:list-column code="assistanceAgent.claim.list-finish.label.status" path="status" width="20%"/>
	<acme:list-column code="assistanceAgent.claim.list-finish.label.leg.flightNumber" path="leg.flightNumber" width="20%"/>
	<acme:list-payload path="payload"/>
</acme:list>
		
<acme:button code="assistanceAgent.claim.list-finish.button.create" action="/assistance-agent/claim/create"/>