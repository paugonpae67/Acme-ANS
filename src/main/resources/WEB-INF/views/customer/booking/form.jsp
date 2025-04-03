<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="${!draftMode}">
	<acme:input-textbox code="customer.booking.form.label.locatorCode" path="locatorCode"/>	
	<acme:input-moment code="customer.booking.form.label.purchaseMoment" path="purchaseMoment" readonly="true"/>
	<acme:input-money code="customer.booking.form.label.price" path="price" readonly="true"/>
	<acme:input-textbox code="customer.booking.form.label.lastNibble" path="lastNibble"/>

	
	<jstl:if test="${draftMode}">
		<acme:input-select code="customer.booking.form.label.travelClass" path="travelClass" choices="${travelClassOptions}"/>
		<acme:input-select code="customer.booking.form.label.flight" path="flight" choices="${flightsOptions}"/>
	</jstl:if>
	<jstl:if test="${!draftMode}">
		<acme:input-textbox code="customer.booking.form.label.travelClass" path="travelClass"/>
		<acme:input-textbox code="customer.booking.form.label.flight" path="flight"/>
	</jstl:if>
	
	
	<jstl:choose>
	
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')}">
			<jstl:if test="${draftMode}">
				<acme:input-checkbox code="customer.booking.form.label.confirmation" path="confirmation"/>
				<acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
				<acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
				<acme:button code="customer.booking.form.button.booking-record" action="/customer/booking-record/list?bookingId=${id}"/>
			</jstl:if>
		</jstl:when>
		
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
		</jstl:when>	
			
	</jstl:choose>	

	
	
</acme:form>