<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 

     <acme:hidden-data path="taskId"/>
     
	<acme:input-select code="technician.task.form.label.type" path="type" choices="${tasks}" />
    <acme:input-integer path="priority" code="technician.task.form.label.priority" />
    <acme:input-textarea code="technician.task.form.label.description" path="description" />
    <acme:input-integer code="technician.task.form.label.estimated-duration" path="estimatedDuration" />

	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')  && draftMode == true}">
			<acme:submit code="technician.task.form.button.update" action="/technician/task/update"/>
			<acme:submit code="technician.task.form.button.publish" action="/technician/task/publish"/>
			<acme:submit code="technician.task.form.button.delete" action="/technician/task/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="technician.task.form.button.create" action="/technician/task/create"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>
