<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="${true}"> 

	<acme:input-moment code="assistanceAgent.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.passengerEmail" path="passengerEmail"/>
	<acme:input-textarea code="assistanceAgent.claim.form.label.description" path="description"/>
	<acme:input-select code="assistanceAgent.claim.form.label.type" path="type" choices="${types}" />
	<acme:input-select code="assistanceAgent.claim.form.label.leg" path="leg" choices="${legs}" />
	<acme:input-textbox code="assistanceAgent.claim.form.label.status" path="status" readonly="true"/>
	
	<acme:button code="assistanceAgent.claim.form.button.tracking-log" action="/administrator/tracking-log/list?masterId=${id}"/>
		
</acme:form>
