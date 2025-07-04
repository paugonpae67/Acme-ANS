<%--
- menu.jsp
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
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.link-Paula" action="https://www.youtube.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.link-Lidia" action="https://www.netflix.com/es/"/>
			<acme:menu-suboption code="master.menu.anonymous.link-Fernando" action="https://www.twitch.tv/"/>
			<acme:menu-suboption code="master.menu.anonymous.link-Iratxe" action="https://www.spotify.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.link-Maria" action="https://www.max.com/"/>
			
		</acme:menu-option>

		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
			<acme:menu-separator/>

			<acme:menu-suboption code="master.menu.administrator.list-airlines" action="/administrator/airline/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.claim.list" action="/administrator/claim/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.aircraft.list" action="/administrator/aircraft/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list" action="/administrator/airport/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.maintenanceRecord.list" action="/administrator/maintenance-record/list"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">

			<acme:menu-suboption code="master.menu.customer.booking.list" action="/customer/booking/list"/>
			<acme:menu-suboption code="master.menu.customer.passenger.list" action="/customer/passenger/list"/>

		</acme:menu-option>

		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.claim" access="hasRealm('AssistanceAgent')">		
    		<acme:menu-suboption code="master.menu.claim.list-finish" action="/assistance-agent/claim/list-finish" />
    		<acme:menu-suboption code="master.menu.claim.list-undergoing" action="/assistance-agent/claim/list-undergoing" />
    		<acme:menu-suboption code="master.menu.claim.show-dashboard" action="/assistance-agent/assistance-agent-dashboard/show"/>	
		</acme:menu-option>


		
		<acme:menu-option code="master.menu.flight-assignment" access="hasRealm('FlightCrewMember')">
			<acme:menu-suboption code="master.menu.flight-assignment.list-past" action="/flight-crew-member/flight-assignment/list-past"/>
			<acme:menu-suboption code="master.menu.flight-assignment.list-future" action="/flight-crew-member/flight-assignment/list-future"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.maintenanceRecord.list" action="/technician/maintenance-record/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.task.list" action="/technician/task/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.show-dashboard" action="/technician/technician-dashboard/show"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.manager" access="hasRealm('Manager')">
			<acme:menu-suboption code="master.menu.manager.flight.list" action="/manager/flight/list"/>
    	</acme:menu-option>
		

	</acme:menu-left>

	<acme:menu-right>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-provider" action="/authenticated/provider/create" access="!hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.provider-profile" action="/authenticated/provider/update" access="hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.become-consumer" action="/authenticated/consumer/create" access="!hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-assistance-agent" action="/authenticated/assistance-agent/create" access="!hasRealm('AssistanceAgent')"/>
			<acme:menu-suboption code="master.menu.user-account.consumer-profile" action="/authenticated/consumer/update" access="hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-technician" action="/authenticated/technician/create" access="!hasRealm('Technician')"/>
			<acme:menu-suboption code="master.menu.user-account.technician-profile" action="/authenticated/technician/update" access="hasRealm('Technician')"/>
			<acme:menu-suboption code="master.menu.user-account.assistance-agent-profile" action="/authenticated/assistance-agent/update" access="hasRealm('AssistanceAgent')"/>
		</acme:menu-option>
		
		
	</acme:menu-right>
</acme:menu-bar>

