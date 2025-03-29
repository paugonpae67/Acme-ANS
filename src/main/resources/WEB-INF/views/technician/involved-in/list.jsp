<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.involved-in.list.label.priority" path="priority" width="10%"/>
	<acme:list-column code="technician.involved-in.list.label.description" path="description" width="30%"/>
	<acme:list-payload path="involvedIn"/>
</acme:list>

<jstl:if test="${showCreate}">
	<acme:button code="technician.involved-in.list.button.create" action="/technician/involved-in/create?masterId=${masterId}"/>
</jstl:if>


