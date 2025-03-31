<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	
	<acme:input-moment code="FlightCrewMember.ActivityLog.form.label.typeOfIncident" path="typeOfIncident"/>
	<acme:input-textbox code="FlightCrewMember.ActivityLog.form.label.registrationMoment" path="registrationMoment"/>
	<acme:input-textbox code="FlightCrewMember.ActivityLog.form.label.saverityLevel" path="saverityLevel"/>
	<acme:input-textarea code="FlightCrewMember.ActivityLog.form.label.description" path="description"/>
	
		
	
</acme:form>