
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
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		Claim claim;

		try {
			//if (!super.getRequest().getMethod().equals("POST"))
			//	super.getResponse().setAuthorised(false);
			//else {
			claimId = super.getRequest().getData("masterId", int.class);
			claim = this.repository.findClaimById(claimId);
			status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

			super.getResponse().setAuthorised(status);
			//}

		} catch (Exception e) {
			super.getResponse().setAuthorised(false);
		}
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
		List<TrackingLog> trackingLogs = this.repository.findTrackingLogOfClaim(trackingLog.getClaim().getId());
		List<TrackingLog> beforeActual = trackingLogs.stream().filter(t -> t.getId() != trackingLog.getId()).filter(t -> !t.getLastUpdateMoment().after(trackingLog.getLastUpdateMoment())).collect(Collectors.toList());

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null && trackingLog.getResolution() != null) {
			Double percentage = trackingLog.getResolutionPercentage();
			TrackingLogStatus status = trackingLog.getStatus();
			String resolution = trackingLog.getResolution();
			Long countPercentage = trackingLogs.stream().filter(x -> !x.getResolutionPercentage().equals(100.00)).filter(x -> !Objects.equals(x.getId(), trackingLog.getId()))
				.filter(x -> Objects.equals(x.getResolutionPercentage(), trackingLog.getResolutionPercentage())).count();

			if (countPercentage > 0)
				super.state(false, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.notSamePercentage");

			if (!percentage.equals(100.00))
				super.state(status.equals(TrackingLogStatus.PENDING), "status", "assistanceAgent.trackingLog.form.error.statusWrongNotFinished");
			else
				super.state(!status.equals(TrackingLogStatus.PENDING), "status", "assistanceAgent.trackingLog.form.error.statusWrongFinished");

			if (status.equals(TrackingLogStatus.ACCEPTED) || status.equals(TrackingLogStatus.REJECTED)) {
				boolean hasResolution = resolution != null && !resolution.isBlank();
				super.state(hasResolution, "resolution", "assistanceAgent.trackingLog.form.error.resolutionNeeded");
			}

			if (trackingLog.getLastUpdateMoment().compareTo(trackingLog.getClaim().getRegistrationMoment()) < 0)
				super.state(false, "lastUpdateMoment", "assistanceAgent.trackingLog.form.error.lastUpdateMoment");

			boolean morePercentage = true;
			if (!beforeActual.isEmpty()) {
				beforeActual.sort(Comparator.comparing(TrackingLog::getResolutionPercentage).reversed());
				TrackingLog previous = beforeActual.get(0);

				Long maxComplete = beforeActual.stream().filter(x -> x.getResolutionPercentage() != null && x.getResolutionPercentage().equals(100.00)).count();

				if (percentage.equals(100.00)) {
					if (maxComplete == 1) {
						super.state(status.equals(previous.getStatus()), "status", "assistanceAgent.trackingLog.form.error.statusNewPercentageFinished");
						super.state(!trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00), "*", "assistanceAgent.trackingLog.form.error.createTwoTrackingLogFinishedClaimPublished");
						super.state(!trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00) && !previous.isDraftMode(), "*", "assistanceAgent.trackingLog.form.error.createTwoTrackingLogFinishedTheBeforePublished");
					} else if (maxComplete >= 2)
						super.state(false, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.completePercentage");

				} else {
					morePercentage = trackingLog.getResolutionPercentage() > previous.getResolutionPercentage();
					super.state(morePercentage, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.wrongNewPercentage");

				}
			}
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
