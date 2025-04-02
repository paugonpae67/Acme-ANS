<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="customer.booking-record.form.label.passenger" path="passenger" choices="${passengersOptions}"/>
			<acme:submit code="customer.booking-record.form.button.create" action="/customer/booking-record/create?bookingId=${booking.id}"/>
			
		</jstl:when>
		
		<jstl:when test="${_command == 'show'}">
			<acme:input-textbox code="customer.booking-record.form.label.booking" path="bookingLocatorCode" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.passengerName" path="passengerName" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.passengerEmail" path="passengerEmail" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.customer" path="customerCreator" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.isPublished" path="passengerPublished" readonly="true"/>
		</jstl:when>		
	</jstl:choose>	
	
	
</acme:form>