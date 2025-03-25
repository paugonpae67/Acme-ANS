<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-moment code="technician.maintenanceRecord.form.label.maintenanceMoment" path="maintenanceMoment"/>
	<acme:input-select code="technician.maintenanceRecord.form.label.status" path="status" choices="${statuses}"/>	
	<acme:input-moment code="technician.maintenanceRecord.form.label.nextInspection" path="nextInspection"/>
	<acme:input-money code="technician.maintenanceRecord.form.label.estimatedCost" path="estimatedCost"/>
	<acme:input-textbox code="technician.maintenanceRecord.form.label.notes" path="notes"/>

	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')}">
			<acme:button code="technician.maintenanceRecord.form.button.maintenaceRecords" action="/technician/maintenance-record/list?masterId=${id}"/>
			<acme:submit code="technician.maintenanceRecord.form.button.update" action="/technician/maintenance-record/update"/>
			<acme:submit code="technician.maintenanceRecord.form.button.publish" action="/technician/maintenance-record/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="technician.maintenanceRecord.form.button.create" action="/technician/maintenance-record/create"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>