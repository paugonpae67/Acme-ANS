<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.airport.list.label.name" path="name" width="20%"/>
	<acme:list-column code="administrator.airport.list.label.iataCode" path="iataCode" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.city" path="title" width="70%"/>
	<acme:list-payload path="airports"/>	
</acme:list>

<acme:button code="administrator.airport.list.button.create" action="/administrator/airport/create"/>