<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode && claimDraftMode}">
			<acme:input-moment code="assistanceAgent.trackingLog.form.label.lastUpdateMoment" path="lastUpdateMoment" readonly="true"/>
			<acme:input-textbox code="assistanceAgent.trackingLog.form.label.step" path="step"/>
			<acme:input-double code="assistanceAgent.trackingLog.form.label.resolutionPercentage" path="resolutionPercentage" placeholder="assistance-agent.tracking-log.form.placeholder.resolutionPercentage"/>
			<acme:input-textarea code="assistanceAgent.trackingLog.form.label.resolution" path="resolution"/>
			<acme:input-select code="assistanceAgent.trackingLog.form.label.status" path="status" choices="${statuses}"/>
			
			<acme:submit code="assistanceAgent.trackingLog.form.button.update" action="/assistance-agent/tracking-log/update"/>
			<acme:submit code="assistanceAgent.trackingLog.form.button.delete" action="/assistance-agent/tracking-log/delete"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode && !claimDraftMode}">
			<acme:input-moment code="assistanceAgent.trackingLog.form.label.lastUpdateMoment" path="lastUpdateMoment" readonly="true"/>
			<acme:input-textbox code="assistanceAgent.trackingLog.form.label.step" path="step"/>
			<acme:input-double code="assistanceAgent.trackingLog.form.label.resolutionPercentage" path="resolutionPercentage" placeholder="assistance-agent.tracking-log.form.placeholder.resolutionPercentage"/>
			<acme:input-textarea code="assistanceAgent.trackingLog.form.label.resolution" path="resolution"/>
			<acme:input-select code="assistanceAgent.trackingLog.form.label.status" path="status" choices="${statuses}"/>
			
			<acme:submit code="assistanceAgent.trackingLog.form.button.update" action="/assistance-agent/tracking-log/update"/>
			<acme:submit code="assistanceAgent.trackingLog.form.button.delete" action="/assistance-agent/tracking-log/delete"/>
			<acme:submit code="assistanceAgent.trackingLog.form.button.publish" action="/assistance-agent/tracking-log/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'show' && !draftMode}">
			<acme:input-moment code="assistanceAgent.trackingLog.form.label.lastUpdateMoment" path="lastUpdateMoment" readonly="true"/>
			<acme:input-textbox code="assistanceAgent.trackingLog.form.label.step" path="step" readonly="true"/>
			<acme:input-double code="assistanceAgent.trackingLog.form.label.resolutionPercentage" path="resolutionPercentage" placeholder="assistance-agent.tracking-log.form.placeholder.resolutionPercentage" readonly="true"/>
			<acme:input-textarea code="assistanceAgent.trackingLog.form.label.resolution" path="resolution" readonly="true"/>
			<acme:input-select code="assistanceAgent.trackingLog.form.label.status" path="status" choices="${statuses}" readonly="true"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-textbox code="assistanceAgent.trackingLog.form.label.step" path="step"/>
			<acme:input-double code="assistanceAgent.trackingLog.form.label.resolutionPercentage" path="resolutionPercentage" placeholder="assistance-agent.tracking-log.form.placeholder.resolutionPercentage"/>
			<acme:input-textarea code="assistanceAgent.trackingLog.form.label.resolution" path="resolution"/>
			<acme:input-select code="assistanceAgent.trackingLog.form.label.status" path="status" choices="${statuses}"/>
			
			<acme:submit code="assistanceAgent.trackingLog.form.button.create" action="/assistance-agent/tracking-log/create?masterId=${claimId}"/>
		</jstl:when>
	</jstl:choose>
</acme:form>