<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.booking.list.label.locatorCode" path="locatorCode" width="20%"/>
	<acme:list-column code="customer.booking.list.label.purchaseMoment" path="purchaseMoment" width="10%"/>
	<acme:list-column code="customer.booking.list.label.travelClass" path="travelClass" width="10%"/>
	<acme:list-column code="customer.booking.list.label.flight" path="flight" width="20%"/>
	<acme:list-payload path="bookings"/>	
</acme:list>

<acme:button code="customer.booking.list.button.create" action="/customer/booking/create"/>