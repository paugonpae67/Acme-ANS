<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
	<acme:print code="technician.dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.recordWithNearestInspection"/>
		</th>
		<td>
			<acme:print value="${recordWithNearestInspection}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.top5AircraftsWithMostTasks"/>
		</th>
		<td>
			<jstl:forEach var="aircraft" items="${top5AircraftsWithMostTasks}">
				<acme:print value="${aircraft.id}"/><br/>
			</jstl:forEach>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.averageEstimatedCostLastYear"/>
		</th>
		<td>
			<acme:print value="${averageEstimatedCostLastYear}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.minimumEstimatedCostLastYear"/>
		</th>
		<td>
			<acme:print value="${minimumEstimatedCostLastYear}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.maximumEstimatedCostLastYear"/>
		</th>
		<td>
			<acme:print value="${maximumEstimatedCostLastYear}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.sTDDEVEstimatedCostLastYear"/>
		</th>
		<td>
			<acme:print value="${sTDDEVEstimatedCostLastYear}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.averageEstimatedDurationTask"/>
		</th>
		<td>
			<acme:print value="${averageEstimatedDurationTask}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.minimumEstimatedDurationTask"/>
		</th>
		<td>
			<acme:print value="${minimumEstimatedDurationTask}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.maximumEstimatedDurationTask"/>
		</th>
		<td>
			<acme:print value="${maximumEstimatedDurationTask}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.sTDDEVEstimatedDurationTask"/>
		</th>
		<td>
			<acme:print value="${sTDDEVEstimatedDurationTask}"/>
		</td>
	</tr>
</table>

<h2>
	<acme:print code="technician.dashboard.form.label.maintenanceRecord-statuses"/>
</h2>

<div>
	<canvas id="canvas"></canvas>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		var data = {
			labels : [
					"PENDING", "IN_PROGRESS", "COMPLETED"
			],
			datasets : [
				{
					data : [
						<jstl:out value="${numberMaintenanceRecordPending}"/>, 
						<jstl:out value="${numberMaintenanceRecordInProgress}"/>, 
						<jstl:out value="${numberMaintenanceRecordCompleted}"/>
					]
				}
			]
		};
		var options = {
			scales : {
				yAxes : [
					{
						ticks : {
							suggestedMin : 0.0,
							suggestedMax : 1.0
						}
					}
				]
			},
			legend : {
				display : false
			}
		};
	
		var canvas, context;
	
		canvas = document.getElementById("canvas");
		context = canvas.getContext("2d");
		new Chart(context, {
			type : "bar",
			data : data,
			options : options
		});
	});
</script>

<acme:return/>