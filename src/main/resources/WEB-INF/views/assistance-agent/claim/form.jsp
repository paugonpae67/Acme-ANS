<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="assistanceAgent.claim.form.label.registrationMoment" path="registrationMoment"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.passengerEmail" path="passengerEmail"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.description" path="description"/>
	<acme:input-select code="assistanceAgent.claim.form.label.type" path="type" choices="${types}" />
	<acme:input-select code="assistanceAgent.claim.form.label.leg" path="leg" choices="${legs}" />
	
	<jstl:if test="${_command == 'create'}">
		<acme:submit code="assistanceAgent.claim.form.button.create" action="/assistance-agent/claim/create"/>
	</jstl:if>
	
	<jstl:if test="${_command == 'update'}">
		<acme:submit code="assistanceAgent.consumer.form.button.update" action="/authenticated/consumer/update"/>
	</jstl:if>
</acme:form>