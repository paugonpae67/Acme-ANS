<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="${false}">
	<acme:input-textbox code="assistanceAgent.claim.form.label.registrationMoment" path="registrationMoment"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.passengerEmail" path="passengerEmail"/>
	<acme:input-textarea code="assistanceAgent.claim.form.label.description" path="description"/>
	<acme:input-select code="assistanceAgent.claim.form.label.type" path="type" choices="${types}" />
	<acme:input-textbox code="assistanceAgent.claim.form.label.status" path="status" readonly="true"/>
	<acme:input-select code="assistanceAgent.trackingLog.form.label.leg" path="leg" choices="${legs}" />
	
	<jstl:choose>
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="assistanceAgent.claim.form.button.tracking-log" action="/assistance-agent/tracking-log/list?masterId=${id}"/>			
		</jstl:when>
	
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish')}">
		<jstl:if test="${draftMode}">
			<acme:submit code="assistanceAgent.claim.form.button.update" action="/assistance-agent/claim/update"/>
			<acme:submit code="assistanceAgent.claim.form.button.publish" action="/assistance-agent/claim/publish"/>
			<acme:submit code="assistanceAgent.claim.form.button.delete" action="/assistance-agent/claim/delete"/>
			<acme:button code="assistanceAgent.claim.form.button.tracking-log" action="/assistance-agent/tracking-log/list?masterId=${id}"/>
		</jstl:if>
	</jstl:when>
		
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="assistanceAgent.claim.form.label.leg" path="leg" choices="${legs}" />
			<acme:submit code="assistanceAgent.claim.form.button.create" action="/assistance-agent/claim/create"/>
		</jstl:when>
		
	</jstl:choose>
</acme:form>