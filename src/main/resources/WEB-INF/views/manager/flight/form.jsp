<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="acme" uri="http://acme-framework.org/" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Flight Details</title>
</head>
<body>
    <acme:form readonly="false">
        <acme:input-textbox code="manager.flight.form.label.tag" path="tag" />
        <acme:input-select path="indication" choices="${indications}" code="manager.flight.form.label.indication"/>
        <acme:input-textbox code="manager.flight.form.label.cost" path="cost" />
        <acme:input-textbox code="manager.flight.form.label.description" path="description" />

        <c:if test="${_command != 'create'}">
            <acme:input-moment code="manager.flight.form.label.scheduledDeparture" readonly="true" path="scheduledDeparture" />
            <acme:input-moment code="manager.flight.form.label.scheduledArrival" readonly="true" path="scheduledArrival" />
            <acme:input-textbox code="manager.flight.form.label.originCity" readonly="true" path="originCity" />
            <acme:input-textbox code="manager.flight.form.label.destinationCity" readonly="true" path="destinationCity" />
            <acme:input-textbox code="manager.flight.form.label.numberOfLayovers" readonly="true" path="numberOfLayovers" />
        </c:if>

        <c:if test="${draftMode == true}">
            <acme:submit code="manager.flight.form.button.publish" action="/manager/flight/publish" />
        </c:if>

        <c:choose>
    <c:when test="${_command == 'create'}">
        <acme:submit code="manager.flight.form.button.create" action="/manager/flight/create" />
    </c:when>
</c:choose>

<c:if test="${draftMode}">
    <acme:submit code="manager.flight.form.button.update" action="/manager/flight/update" />
    <acme:submit code="manager.flight.form.button.delete" action="/manager/flight/delete" />
</c:if>


        <c:if test="${_command != 'create'}">
            <acme:button code="manager.flight.form.button.legs" action="/manager/leg/list?flightId=${id}" />
        </c:if>
    </acme:form>
</body>
</html>
