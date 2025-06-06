<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="customer.booking-record.form.label.createPassenger" path="passenger" choices="${passengersOptions}"/>
			<acme:submit code="customer.booking-record.form.button.create" action="/customer/booking-record/create?bookingId=${booking.id}"/>
			
		</jstl:when>
		
		<jstl:when test="${_command == 'delete'}">
			<acme:input-select code="customer.booking-record.form.label.deletePassenger" path="passenger" choices="${passengersChoices}"/>
			
			<jstl:if test="${!bookingPublished}">
				<acme:input-checkbox code="customer.booking-record.form.label.confirmation" path="confirmation"/>		
			</jstl:if>
			
			<acme:submit code="customer.booking-record.form.button.delete" action="/customer/booking-record/delete?bookingId=${booking.id}"/>
			
			
		</jstl:when>
		
		<jstl:when test="${_command =='show'}">
			<acme:input-textbox code="customer.booking-record.form.label.booking" path="bookingLocatorCode" readonly="true"/>
			
			<acme:input-textbox code="customer.booking-record.form.label.passengerName" path="passengerName" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.passengerEmail" path="passengerEmail" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.passportNumber" path="passportNumber" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.dateOfBirth" path="dateOfBirth" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.specialNeeds" path="specialNeeds" readonly="true"/>
			
			<acme:input-textbox code="customer.booking-record.form.label.customer" path="customerCreator" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.isPublished" path="passengerPublished" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.bookingId" path="bookingId" readonly="true"/>
			<acme:input-textbox code="customer.booking-record.form.label.passengerId" path="passengerId" readonly="true"/>	
					
		</jstl:when>
				
	</jstl:choose>	
	
	
</acme:form>