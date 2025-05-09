
package acme.features.assistanceAgent.trakingLog;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(claimId);
		status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int claimId;
		Claim claim;
		Date moment;

		claimId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(claimId);

		moment = MomentHelper.getCurrentMoment();

		trackingLog = new TrackingLog();
		trackingLog.setLastUpdateMoment(moment);
		trackingLog.setClaim(claim);
		trackingLog.setStatus(TrackingLogStatus.PENDING);
		trackingLog.setResolutionPercentage(0.00);
		trackingLog.setDraftMode(true);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "resolution", "status");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		List<TrackingLog> trackingLogs = this.repository.findLatestTrackingLogByClaimExceptItself(trackingLog.getClaim().getId(), trackingLog.getId()).orElse(List.of());

		TrackingLog latestTrackingLog = trackingLogs.stream().findFirst().orElse(null);

		if (!trackingLogs.isEmpty()) {
			Double minPercentage = trackingLogs.stream().map(TrackingLog::getResolutionPercentage).filter(p -> p != null).min(Double::compare).orElse(0.00);

			long maximumTrackingLogs = trackingLogs.stream().filter(x -> x.getResolutionPercentage() != null && x.getResolutionPercentage().equals(100.00)).count();

			Long countPercentage = trackingLogs.stream().filter(x -> x.getResolutionPercentage() != null && Objects.equals(x.getResolutionPercentage(), trackingLog.getResolutionPercentage())).count();
			super.state(countPercentage == 0, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.wrongPercentage");

			if (maximumTrackingLogs == 0) {
				//super.state(trackingLog.getResolutionPercentage() > minPercentage, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.wrongNewPercentage");
				if (trackingLog.getResolutionPercentage() != null && trackingLog.getResolutionPercentage() < 100.0) {
					boolean badStatus = !trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
					super.state(!badStatus, "status", "assistanceAgent.trackingLog.form.error.wrongStatus");
				} else if (trackingLog.getStatus() != null) {
					boolean badStatus = trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
					super.state(!badStatus, "status", "assistanceAgent.trackingLog.form.error.wrongStatus2");
				}

				if (trackingLog.getStatus() != null && !trackingLog.getStatus().equals(TrackingLogStatus.PENDING)) {
					boolean hasResolution = trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank();
					super.state(hasResolution, "resolution", "assistanceAgent.trackingLog.form.error.resolutionNeeded");
				}

			} else if (maximumTrackingLogs == 1) {

				if (trackingLog.getResolutionPercentage() != null && trackingLog.getResolutionPercentage() < 100.0) {
					boolean badStatus = !trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
					super.state(!badStatus, "status", "assistanceAgent.trackingLog.form.error.wrongStatus");
				} else {
					boolean badStatus = trackingLog.getStatus() != null && trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
					super.state(!badStatus, "status", "assistanceAgent.trackingLog.form.error.wrongStatus2");

					boolean sameStatus = latestTrackingLog != null && trackingLog.getResolutionPercentage().equals(100.00) && trackingLog.getStatus().equals(latestTrackingLog.getStatus());
					super.state(sameStatus, "status", "assistanceAgent.trackingLog.form.error.statusNewPercentageTotal");

					boolean claimPublished = latestTrackingLog != null && !trackingLog.getClaim().isDraftMode() && latestTrackingLog.getResolutionPercentage() != null && latestTrackingLog.getResolutionPercentage().equals(100.00);
					super.state(claimPublished, "draftMode", "No se puede crear dos trackingLog con 100% si la claim no se ha publicado.");
				}

				if (trackingLog.getStatus() != null && !trackingLog.getStatus().equals(TrackingLogStatus.PENDING)) {
					boolean hasResolution = trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank();
					super.state(hasResolution, "resolution", "assistanceAgent.trackingLog.form.error.resolutionNeeded");
				}

			} else if (maximumTrackingLogs >= 2)
				super.state(false, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.complatePercentage");
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statuses;
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "resolution");

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("statuses", statuses);
		dataset.put("claimId", super.getRequest().getData("masterId", int.class));
		dataset.put("draftMode", trackingLog.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
