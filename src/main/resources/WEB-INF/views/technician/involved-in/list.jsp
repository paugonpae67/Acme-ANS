<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
<acme:list-column code="technician.involves.list.label.ticker" path="ticker" width="30%"/>
	<acme:list-column code="technician.involved-in.list.label.priority" path="priority" width="30%"/>
	<acme:list-column code="technician.involved-in.list.label.task" path="task" width="30%"/>
	
</acme:list>	
	
<jstl:if test="${showCreate}">
	<acme:button code="technician.involved-in.form.button.create" action="/technician/involved-in/create?masterId=${masterId}"/>
	<acme:button code="technician.involved-in.form.button.delete" action="/technician/involved-in/delete-form?masterId=${masterId}"/>
</jstl:if>


