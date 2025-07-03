<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="true">
	<acme:input-double code="assistanceAgent.dashboard.form.label.numberofClaimsResolvedSuccessfully" path="numberofClaimsResolvedSuccessfully"/>	
	<acme:input-double code="assistanceAgent.dashboard.form.label.numberofClaimsRejected" path="numberofClaimsRejected"/>	
	<acme:input-textbox code="assistanceAgent.dashboard.form.label.top3MonthsHighestNumberClaims" path="top3MonthsHighestNumberClaims"/>	
	<acme:input-double code="assistanceAgent.dashboard.form.label.avergeNumberLogsClaimsHave" path="avergeNumberLogsClaimsHave" />
	<acme:input-integer code="assistanceAgent.dashboard.form.label.minimumNumberLogsClaimsHave" path="minimumNumberLogsClaimsHave" />
	<acme:input-integer code="assistanceAgent.dashboard.form.label.maximumNumberLogsClaimsHave" path="maximumNumberLogsClaimsHave" />
	<acme:input-double code="assistanceAgent.dashboard.form.label.standardDeviationNumberLogsClaimsHave" path="standardDeviationNumberLogsClaimsHave"/>	
	<acme:input-double code="assistanceAgent.dashboard.form.label.avergeNumberClaimsAssistedLastMonth" path="avergeNumberClaimsAssistedLastMonth"/>	
	<acme:input-integer code="assistanceAgent.dashboard.form.label.minimumNumberClaimsAssistedLastMonth" path="minimumNumberClaimsAssistedLastMonth"/>	
	<acme:input-integer code="assistanceAgent.dashboard.form.label.maximumNumberClaimsAssistedLastMonth" path="maximumNumberClaimsAssistedLastMonth" />
	<acme:input-double code="assistanceAgent.dashboard.form.label.standardDeviationNumberClaimsAssistedLastMonth" path="standardDeviationNumberClaimsAssistedLastMonth" />
	
		
</acme:form>