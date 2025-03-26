<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="acme" uri="http://acme-framework.org/" %>

<acme:list>
    <acme:list-column path="name" code="airline.name" />
    <acme:list-column path="iataCode" code="airline.iata-code" />
    <acme:list-column path="website" code="airline.website" />
    <acme:list-column path="type" code="airline.type" />
    <acme:list-column path="foundationMoment" code="airline.foundation-moment" />
    <acme:list-column path="email" code="airline.email" />
    <acme:list-column path="phoneNumber" code="airline.phone-number" />
    <acme:list-column path="hub.name" code="airline.hub" />
</acme:list>

<acme:button code="airline.create" action="/administrator/airline/create" />
