<%--
- list.jsp
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


<h2>  
     <acme:print code="technician.dashboard.form.title"/>  
</h2>  

<table class="table table-sm">  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.number-maintenance-record"/>  
         </th>  
         <td>  
             <acme:print value="numberMaintenanceRecord"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.record-with-nearest-inspection-due"/>  
         </th>  
         <td>  
             <acme:print value="recordWithNearestInspectionDue"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.top-5-Aircraft-with-most-task"/>  
         </th>  
         <td>  
             <acme:print value="top5AircraftsWithMostTasks"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.average-estimated-cost-last-year"/>  
         </th>  
         <td>  
             <acme:print value="averageEstimatedCostLastYear"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.minimum-estimated-cost-last-year"/>  
         </th>  
         <td>  
             <acme:print value="minimumEstimatedCostLastYear"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.maximum-estimated-cost-last-year"/>  
         </th>  
         <td>  
             <acme:print value="maximumEstimatedCostLastYear"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.STDDEV-estimated-cost-last-year"/>  
         </th>  
         <td>  
             <acme:print value="sTDDEVEstimatedCostLastYear"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.average-estimated-duration-task"/>  
         </th>  
         <td>  
             <acme:print value="averageEstimatedDurationTask"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.minimum-estimated-duration-task"/>  
         </th>  
         <td>  
             <acme:print value="minimumEstimatedDurationTask"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.maximum-estimated-duration-task"/>  
         </th>  
         <td>  
             <acme:print value="maximumEstimatedDurationTask"/>  
         </td>  
     </tr>  
     <tr>  
         <th scope="row">  
             <acme:print code="technician.dashboard.form.label.STDDEV-estimated-duration-task"/>  
         </th>  
         <td>  
             <acme:print value="sTDDEVEstimatedDurationTask"/>  
         </td>  
     </tr>  

 </table>  
 
 <div>  
     <canvas id="canvas"></canvas>  
 </div>  
 
 <acme:return/>  