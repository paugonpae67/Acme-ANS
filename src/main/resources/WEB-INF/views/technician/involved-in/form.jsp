<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:hidden-data path="id"/>
	<acme:input-select code="technician.involved-in.form.label.task" path="task" readonly="${_command == 'show'}" choices="${tasks}"/>

	<jstl:if test="${_command == 'show'}">	
		<acme:input-textbox code="technician.involved-in.form.label.tickerMaintenanceRecord" path="tickerMR" readonly="true"/>
		<acme:input-textbox code="technician.involved-in.form.label.ticker" path="ticker" readonly="true"/>
		<acme:input-select code="technician.involved-in.form.label.type" path="type" choices="${types}" readonly ="true"/>
		<acme:input-integer code="technician.involved-in.form.label.priority" path="priority" readonly="true"/>
		<acme:input-integer code="technician.task.form.label.estimated-duration" path="estimatedDuration" readonly="true"/>
		<acme:input-textbox code="technician.involved-in.form.label.technician" path="technician" readonly="true"/>
	</jstl:if>
	<jstl:choose>	 
		<jstl:when test="${_command == 'delete-form'}">
			<acme:submit code="technician.involved-in.form.button.delete" action="/technician/involved-in/delete?masterId=${masterId}"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="technician.involved-in.form.button.create" action="/technician/involved-in/create?masterId=${masterId}"/>

		</jstl:when>		
	</jstl:choose>
</acme:form>
