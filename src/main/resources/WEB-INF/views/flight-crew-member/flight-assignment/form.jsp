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

<acme:form>
	<acme:input-textbox code="employer.application.form.label.ticker" path="ticker" readonly="true" />
	<acme:input-textbox code="employer.application.form.label.moment" path="moment" readonly="true" />
	<acme:input-textarea code="employer.application.form.label.statement" path="statement" readonly="true" />
	<acme:input-textarea code="employer.application.form.label.skills" path="skills" readonly="true" />
	<acme:input-textarea code="employer.application.form.label.qualifications" path="qualifications" readonly="true" />

	<acme:input-select path="status" code="employer.application.form.label.status" choices="${statuses}" readonly="${acme:anyOf(status, 'ACCEPTED|REJECTED')}" />
	
	<jstl:if test="${acme:anyOf(_command, 'show|update') && !acme:anyOf(status, 'ACCEPTED|REJECTED')}">
		<acme:submit code="employer.application.form.button.update" action="/employer/application/update" />
	</jstl:if>
</acme:form>


