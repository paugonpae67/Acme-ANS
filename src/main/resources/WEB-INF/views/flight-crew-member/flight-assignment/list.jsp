

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<!-- Títulos para las dos listas -->
<h2>
    <acme:message code="any.flight-assignment.form.title.completed"/>
</h2>
<acme:list>
    <acme:list-column code="any.flight-assignment.form.label.flightNumber" path="flightNumber" width="20%"/>
    <acme:list-column code="any.flight-assignment.form.label.duty" path="duty" width="20%"/>
    <acme:list-column code="any.flight-assignment.form.label.status" path="status" width="20%"/>
    <acme:list-column code="any.flight-assignment.form.label.scheduledDeparture" path="scheduledDeparture" width="20%"/>
    <acme:list-column code="any.flight-assignment.form.label.scheduledArrival" path="scheduledArrival" width="20%"/>
</acme:list>

<h2>
    <acme:message code="any.flight-assignment.form.title.planned"/>
</h2>
<acme:list>
    <acme:list-column code="any.flight-assignment.form.label.flightNumber" path="flightNumber" width="20%"/>
    <acme:list-column code="any.flight-assignment.form.label.duty" path="duty" width="20%"/>
    <acme:list-column code="any.flight-assignment.form.label.status" path="status" width="20%"/>
    <acme:list-column code="any.flight-assignment.form.label.scheduledDeparture" path="scheduledDeparture" width="20%"/>
    <acme:list-column code="any.flight-assignment.form.label.scheduledArrival" path="scheduledArrival" width="20%"/>
</acme:list>