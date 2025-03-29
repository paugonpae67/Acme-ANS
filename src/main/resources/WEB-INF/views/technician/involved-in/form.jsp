<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
     <jstl:if test="${_command == 'show'}">
		<acme:input-select code="technician.involved-in.form.label.type" path="type" choices="${types}" readonly="true"/>
	    <acme:input-integer path="priority" code="technician.involved-in.form.label.priority" readonly="true"/>
	    <acme:input-textarea code="technician.involved-in.form.label.description" path="description" readonly="true"/>
	    <acme:input-integer code="technician.involved-in.form.label.estimated-duration" path="estimatedDuration" readonly="true"/>
	  </jstl:if>
  
	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|delete')}">
			<acme:submit code="technician.involved-in.form.button.delete" action="/technician/involved-in/delete" />
		</jstl:when>

	</jstl:choose>
</acme:form>
