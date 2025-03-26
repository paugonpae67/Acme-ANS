<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="acme" uri="http://acme-framework.org/" %>

<acme:form>
    <acme:input-textbox path="name" code="airline.name" />
    <acme:input-textbox path="iataCode" code="airline.iata-code" />
    <acme:input-url path="website" code="airline.website" />
    <acme:input-select path="type" code="airline.type" choices="${types}" />
    <acme:input-moment path="foundationMoment" code="airline.foundation-moment" />
    <acme:input-textbox path="email" code="airline.email" />
    <acme:input-textbox path="phoneNumber" code="airline.phone-number" />
    <acme:input-select path="hub" code="airline.hub" choices="${hubs}" />

    <acme:submit code="airline.confirm" action="/administrator/airline/create" />
</acme:form>

