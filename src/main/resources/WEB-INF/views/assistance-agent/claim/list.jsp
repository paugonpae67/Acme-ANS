<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistanceAgent.claim.list-finish.label.registrationMoment" path="registrationMoment" width="10%"/>
	<acme:list-column code="assistanceAgent.claim.list-finish.label.passengerEmail" path="passengerEmail" width="10%"/>
	<acme:list-column code="assistanceAgent.claim.list-finish.label.description" path="description" width="60%"/>
	<acme:list-column code="assistanceAgent.claim.list-finish.label.type" path="type" width="20%"/>
	<acme:list-payload path="finishClaims"/>
</acme:list>

<acme:button code="assistanceAgent.claim.list-finish.button.create" action="/assistance-agent/claim/create"/>
