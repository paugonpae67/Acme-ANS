<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="${!draftMode}">
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.lastUpdateMoment" path="lastUpdateMoment"/>
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.step" path="step"/>
	<acme:input-double code="assistanceAgent.trackingLog.form.label.resolutionPercentage" path="resolutionPercentage"/>
	<acme:input-select code="assistanceAgent.trackingLog.form.label.status" path="status" choices="${statuses}" />
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.resolution" path="resolution"/>
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.claim" path="claim" />
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')}">
		<jstl:if test="${draftMode}">
			<acme:submit code="assistanceAgent.trackingLog.form.button.update" action="/assistance-agent/tracking-log/update"/>
			<acme:submit code="assistanceAgent.trackingLog.form.button.publish" action="/assistance-agent/tracking-log/publish"/>
			<acme:submit code="assistanceAgent.trackingLog.form.button.delete" action="/assistance-agent/tracking-log/delete"/>
			
		</jstl:if>

		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="assistanceAgent.trackingLog.form.button.create" action="/assistance-agent/tracking-log/create"/>
		</jstl:when>
	
				
	</jstl:choose>
</acme:form>