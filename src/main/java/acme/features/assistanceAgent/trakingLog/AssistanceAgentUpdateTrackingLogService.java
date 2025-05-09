
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
public class AssistanceAgentUpdateTrackingLogService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		TrackingLog trackingLog;
		int id;
		AssistanceAgent assistanceAgent;
		Claim claim;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		claim = this.repository.findClaimByTrackingLogId(trackingLog.getId());

		assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();

		status = claim != null && claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(assistanceAgent) && trackingLog != null;

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		Date moment;

		moment = MomentHelper.getCurrentMoment();

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		trackingLog.setLastUpdateMoment(moment);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		List<TrackingLog> trackingLogs = this.repository.findLatestTrackingLogByClaimExceptItself(trackingLog.getClaim().getId(), trackingLog.getId()).orElse(List.of());

		TrackingLog latestTrackingLog = trackingLogs.stream().findFirst().orElse(null);

		if (!trackingLogs.isEmpty()) {
			Double minPercentage = trackingLogs.stream().map(TrackingLog::getResolutionPercentage).filter(p -> p != null).min(Double::compare).orElse(0.00);

			long maximumTrackingLogs = trackingLogs.stream().filter(x -> x.getResolutionPercentage() != null && x.getResolutionPercentage().equals(100.00)).count();

			Long countPercentage = trackingLogs.stream().filter(x -> Objects.equals(x.getResolutionPercentage(), trackingLog.getResolutionPercentage())).count();
			super.state(countPercentage == 0, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.wrongPercentage");

			if (!trackingLog.getClaim().isDraftMode() && !trackingLog.isDraftMode())
				if (trackingLog.getResolutionPercentage() == 100.00) {
					Long publishedtrackingLog = trackingLogs.stream().filter(x -> !x.isDraftMode()).filter(x -> x.getLastUpdateMoment().compareTo(trackingLog.getLastUpdateMoment()) < 0).filter(x -> x.getResolutionPercentage() == 100.00).count();
					super.state(publishedtrackingLog > 0, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.resolutionPercentagePublishTotal");
				} else {
					Long publishedtrackingLogAfter = trackingLogs.stream().filter(x -> !x.isDraftMode()).count();
					super.state(publishedtrackingLogAfter > 0, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.resolutionPercentagePublish");
				}
			if (maximumTrackingLogs == 0) {
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

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("statuses", statuses);
		dataset.put("masterId", trackingLog.getClaim().getId());
		dataset.put("draftMode", trackingLog.isDraftMode());

		super.getResponse().addData(dataset);
	}
}
