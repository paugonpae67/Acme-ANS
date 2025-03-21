<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code=technician.maitenanceRecord.list.label.maintenanceMoment" path="maintenanceMoment" width="10%"/>
	<acme:list-column code="technician.maitenanceRecord.list.label.status" path="status" width="10%"/>
	<acme:list-column code="technician.maitenanceRecord.list.label.nextInspection" path="nextInspection" width="10%"/>
	<acme:list-column code="technician.maitenanceRecord.list.label.estimatedCost" path="estimatedCost" width="10%"/>
	<acme:list-column code="technician.maitenanceRecord.list.label.notes" path="notes" width="10%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="technician.maintenanceRecord.list.button.list" action="/technician/maintenanceRecord/list"/>

</jstl:if>	