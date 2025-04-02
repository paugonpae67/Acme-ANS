<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.booking-record.list.label.passengerName" path="passengerName" width="30%"/>
	<acme:list-column code="customer.booking-record.list.label.passengerEmail" path="passengerEmail" width="30%"/>
</acme:list>

<acme:button code="customer.booking-record.form.button.create" action="/customer/booking-record/create?bookingId=${bookingId}"/>