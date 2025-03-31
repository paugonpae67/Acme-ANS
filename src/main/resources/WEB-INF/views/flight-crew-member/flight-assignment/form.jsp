<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	
	<h5><acme:print code="flight.message.assignment" /></h5>
	
	<acme:input-moment code="FlightCrewMember.FlightAssignment.form.label.moment" path="moment"/>
	<acme:input-textbox code="FlightCrewMember.FlightAssignment.form.label.currentStatus" path="currentStatus"/>
	<acme:input-textbox code="FlightCrewMember.FlightAssignment.form.label.duty" path="duty"/>
	<acme:input-textarea code="FlightCrewMember.FlightAssignment.form.label.remarks" path="remarks"/>
	
	<h5><acme:print code="flight.message.legs" /></h5>
	
	<acme:input-textbox code="FlightCrewMember.FlightAssignment.form.label.flightNumber" path="flightNumber"/>
	<acme:input-moment code="FlightCrewMember.FlightAssignment.form.label.scheduledArrival" path="scheduledArrival"/>
	<acme:input-moment code="FlightCrewMember.FlightAssignment.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-textbox code="FlightCrewMember.FlightAssignment.form.label.status" path="status"/>	
	<acme:input-textbox code="FlightCrewMember.FlightAssignment.form.label.Duration" path="Duration"/>
	
	
	<h5><acme:print code="flight.message.member" /></h5>
	<acme:input-textbox code="FlightCrewMember.FlightAssignment.form.label.employeeCode" path="employeeCode"/>
	<acme:input-textbox code="FlightCrewMember.FlightAssignment.form.label.phoneNumber" path="phoneNumber"/>
	<acme:input-textarea code="FlightCrewMember.FlightAssignment.form.label.languageSkills" path="languageSkills"/>
	<acme:input-double code="FlightCrewMember.FlightAssignment.form.label.salary" path="salary"/>
	<acme:input-textbox code="FlightCrewMember.FlightAssignment.form.label.yearsOfExperience" path="yearsOfExperience"/>
	<acme:input-textbox code="FlightCrewMember.FlightAssignment.form.label.availabilityStatus" path="availabilityStatus"/>
	
	<h6><acme:print code="flight.message.activity" /></h6>
	<acme:list >
    	<acme:list-column code="FlightCrewMember.ActivityLog.list.label.typeOfIncident" path="typeOfIncident" width="10%"/>
    	<acme:list-column code="FlightCrewMember.ActivityLog.list.label.registrationMoment" path="registrationMoment" width="10%"/>
    	<acme:list-column code="FlightCrewMember.ActivityLog.list.label.saverityLevel" path="saverityLevel" width="10%"/>    
	</acme:list>
	
</acme:form>