
package acme.features.assistanceAgent.trakingLog;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
		Integer id;
		Claim claim;

		if (!super.getRequest().getMethod().equals("POST") || super.getRequest().getMethod().equals("POST") && super.getRequest().hasData("masterId", Integer.class))
			super.getResponse().setAuthorised(false);
		else {
			id = super.getRequest().getData("id", Integer.class);
			if (id == null) {
				super.getResponse().setAuthorised(false);
				return;
			}
			trackingLog = this.repository.findTrackingLogById(id);
			if (trackingLog != null) {
				claim = this.repository.findClaimByTrackingLogId(trackingLog.getId());

				status = claim != null && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && trackingLog != null;

				int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
				status = status && assistanceAgentId == claim.getAssistanceAgent().getId();

				super.getResponse().setAuthorised(status);
			} else {
				super.getResponse().setAuthorised(false);
				return;
			}

		}
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
		Date currentMoment = MomentHelper.getCurrentMoment();
		trackingLog.setLastUpdateMoment(currentMoment);
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		List<TrackingLog> trackingLogs = this.repository.findLatestTrackingLogByClaimExceptItself(trackingLog.getClaim().getId(), trackingLog.getId()).orElse(List.of());
		List<TrackingLog> beforeActual = trackingLogs.stream().filter(t -> t.getId() != trackingLog.getId()).filter(t -> !t.getLastUpdateMoment().after(trackingLog.getLastUpdateMoment())).collect(Collectors.toList());
		List<TrackingLog> allTrackingLogs = this.repository.findTrackingLogOfClaim(trackingLog.getClaim().getId());

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null && trackingLog.getResolution() != null) {
			Double percentage = trackingLog.getResolutionPercentage();
			TrackingLogStatus status = trackingLog.getStatus();
			String resolution = trackingLog.getResolution();
			Long countPercentage = trackingLogs.stream().filter(x -> x.getResolutionPercentage() != null && !x.getResolutionPercentage().equals(100.00)).filter(x -> !Objects.equals(x.getId(), trackingLog.getId()))
				.filter(x -> Objects.equals(x.getResolutionPercentage(), trackingLog.getResolutionPercentage())).count();

			if (countPercentage > 0)
				super.state(false, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.notSamePercentage");

			if (percentage != 100)
				super.state(status.equals(TrackingLogStatus.PENDING), "status", "assistanceAgent.trackingLog.form.error.statusWrongNotFinished");
			else
				super.state(!status.equals(TrackingLogStatus.PENDING), "status", "assistanceAgent.trackingLog.form.error.statusWrongFinished");

			if (status.equals(TrackingLogStatus.ACCEPTED) || status.equals(TrackingLogStatus.REJECTED)) {
				boolean hasResolution = resolution != null && !resolution.isBlank();
				super.state(hasResolution, "resolution", "assistanceAgent.trackingLog.form.error.resolutionNeeded");
			}

			boolean morePercentage = true;
			if (!beforeActual.isEmpty()) {
				beforeActual.sort(Comparator.comparing(TrackingLog::getResolutionPercentage).reversed());
				TrackingLog previous = beforeActual.get(0);
				TrackingLog oldTrackingLog = this.repository.findTrackingLogById(super.getRequest().getData("id", int.class));

				if (oldTrackingLog != null && !Objects.equals(oldTrackingLog.getResolutionPercentage(), trackingLog.getResolutionPercentage())) {
					morePercentage = trackingLog.getResolutionPercentage() > previous.getResolutionPercentage();

					super.state(morePercentage, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.wrongNewPercentage");
				}
				Long maxComplete = beforeActual.stream().filter(x -> x.getResolutionPercentage() != null && x.getResolutionPercentage().equals(100.00)).count();

				if (percentage.equals(100.00))
					if (maxComplete == 1) {
						super.state(status.equals(previous.getStatus()), "status", "assistanceAgent.trackingLog.form.error.statusNewPercentageFinished");
						super.state(!trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00), "*", "assistanceAgent.trackingLog.form.error.createTwoTrackingLogFinishedClaimPublished");
						super.state(!trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00) && !previous.isDraftMode(), "*", "assistanceAgent.trackingLog.form.error.createTwoTrackingLogFinishedTheBeforePublished");
					}
			}

		}

	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		trackingLog.setLastUpdateMoment(currentMoment);
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
		dataset.put("claimDraftMode", trackingLog.getClaim().isDraftMode());

		super.getResponse().addData(dataset);
	}
}
