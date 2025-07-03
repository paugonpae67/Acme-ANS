
package acme.features.assistanceAgent.dashboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.forms.AssistanceAgentDashboard;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentDashboardShowService extends AbstractGuiService<AssistanceAgent, AssistanceAgentDashboard> {

	@Autowired
	private AssistanceAgentDashboardRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Claim> claims = this.repository.findAllClaim();
		AssistanceAgentDashboard dashboard = new AssistanceAgentDashboard();

		dashboard.setNumberofClaimsResolvedSuccessfully(Double.NaN);
		dashboard.setNumberofClaimsRejected(Double.NaN);
		dashboard.setAvergeNumberLogsClaimsHave(Double.NaN);
		dashboard.setMaximumNumberClaimsAssistedLastMonth(0);
		dashboard.setMaximumNumberLogsClaimsHave(0L);
		dashboard.setMinimumNumberClaimsAssistedLastMonth(0);
		dashboard.setMinimumNumberLogsClaimsHave(0L);
		dashboard.setStandardDeviationNumberClaimsAssistedLastMonth(Double.NaN);
		dashboard.setStandardDeviationNumberLogsClaimsHave(Double.NaN);
		dashboard.setAvergeNumberClaimsAssistedLastMonth(Double.NaN);

		if (!claims.isEmpty()) {
			long claimsFinishedAccepted = 0;
			long claimsFinishedRejected = 0;

			for (Claim c : claims)
				if (c.getStatus() == TrackingLogStatus.ACCEPTED)
					claimsFinishedAccepted += 1;
				else if (c.getStatus() == TrackingLogStatus.REJECTED)
					claimsFinishedRejected += 1;

			Double numberofClaimsResolvedSuccessfully = (double) claimsFinishedAccepted / this.repository.findAllClaim().stream().count();
			dashboard.setNumberofClaimsResolvedSuccessfully(numberofClaimsResolvedSuccessfully);

			double numberofClaimsRejected = (double) claimsFinishedRejected / this.repository.findAllClaim().stream().count();
			dashboard.setNumberofClaimsRejected(numberofClaimsRejected);

			SimpleDateFormat monthFormat = new SimpleDateFormat("MM");

			Collection<Claim> claimsMonth = this.repository.findClaimsOfAMonth();
			Map<String, Long> claimsByMonth = claimsMonth.stream().collect(Collectors.groupingBy(claim -> monthFormat.format(claim.getRegistrationMoment()), Collectors.counting()));

			List<Map.Entry<String, Long>> sortedList = new ArrayList<>(claimsByMonth.entrySet());
			sortedList.sort(Comparator.comparingLong(Map.Entry<String, Long>::getValue).reversed());

			Collection<String> topThreeMonths = sortedList.stream().limit(3).map(Map.Entry::getKey).collect(Collectors.toList());

			dashboard.setTop3MonthsHighestNumberClaims(topThreeMonths);

			long totalTrackingLogs = claims.stream().mapToLong(claim -> this.repository.countLogsByClaimId(claim.getId())).sum();

			double averageNumberOfTrackingLogsClaim = claims.size() > 0 ? (double) totalTrackingLogs / claims.size() : 0.0;
			dashboard.setAvergeNumberLogsClaimsHave(averageNumberOfTrackingLogsClaim);

			Long minTrackingLogs = claims.stream().mapToLong(claim -> this.repository.countLogsByClaimId(claim.getId())).min().orElse(0);
			dashboard.setMinimumNumberLogsClaimsHave(minTrackingLogs);

			Long maxNumberTrackingLogs = claims.stream().mapToLong(claim -> this.repository.countLogsByClaimId(claim.getId())).max().orElse(0);
			dashboard.setMaximumNumberLogsClaimsHave(maxNumberTrackingLogs);

			List<Long> trackingLogsClaim = claims.stream().map(claim -> this.repository.countLogsByClaimId(claim.getId())).collect(Collectors.toList());

			double media = trackingLogsClaim.stream().mapToDouble(Long::doubleValue).average().orElse(0.0);

			double varianza = trackingLogsClaim.stream().mapToDouble(logs -> Math.pow(logs - media, 2)).average().orElse(0.0);

			double standardDeviationNumberLogsClaimsHave = Math.sqrt(varianza);
			dashboard.setStandardDeviationNumberLogsClaimsHave(standardDeviationNumberLogsClaimsHave);

			int esteMes = MomentHelper.getCurrentMoment().getMonth();
			int mesPasado = esteMes - 1;
			int año = MomentHelper.getCurrentMoment().getYear();
			if (esteMes == 1) {
				mesPasado = 12;
				año -= 1;
			}

			Date fecha = MomentHelper.getCurrentMoment();
			fecha.setYear(año);
			fecha.setMonth(mesPasado);

			List<Claim> claimMesPasado = this.repository.findClaimsAfterDate(fecha);
			int totalClaimMesPasado = claimMesPasado.size();
			int numeroAgentes = this.repository.findAllAssistanceAgent().size();
			double average = (double) totalClaimMesPasado / numeroAgentes;
			dashboard.setAvergeNumberClaimsAssistedLastMonth(average);

			Map<AssistanceAgent, Long> claimsAgent = claimMesPasado.stream().collect(Collectors.groupingBy(Claim::getAssistanceAgent, Collectors.counting()));
			int minimumNumberClaimsAssistedLastMonth = claimsAgent.isEmpty() ? 0 : Collections.min(claimsAgent.values()).intValue();
			dashboard.setMinimumNumberClaimsAssistedLastMonth(minimumNumberClaimsAssistedLastMonth);

			int maximumNumberClaimsAssistedLastMonth = claimsAgent.isEmpty() ? 0 : Collections.max(claimsAgent.values()).intValue();
			dashboard.setMaximumNumberClaimsAssistedLastMonth(maximumNumberClaimsAssistedLastMonth);

			double varianzaMesPasado = claimsAgent.values().stream().mapToDouble(count -> Math.pow(count - average, 2)).sum() / (numeroAgentes - 1);

			double standardDeviationClaimsLastMonth = Math.sqrt(varianzaMesPasado);

			dashboard.setStandardDeviationNumberClaimsAssistedLastMonth(standardDeviationClaimsLastMonth);
		}

		super.getBuffer().addData(dashboard);

	}

	@Override
	public void unbind(final AssistanceAgentDashboard object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "numberofClaimsResolvedSuccessfully", "numberofClaimsRejected", "top3MonthsHighestNumberClaims", "avergeNumberLogsClaimsHave", "minimumNumberLogsClaimsHave", "maximumNumberLogsClaimsHave",
			"standardDeviationNumberLogsClaimsHave", "avergeNumberClaimsAssistedLastMonth", "minimumNumberClaimsAssistedLastMonth", "maximumNumberClaimsAssistedLastMonth", "standardDeviationNumberClaimsAssistedLastMonth");

		super.getResponse().addData(dataset);
	}

}
