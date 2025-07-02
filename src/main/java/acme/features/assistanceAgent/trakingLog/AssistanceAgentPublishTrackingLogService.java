
package acme.features.assistanceAgent.trakingLog;

import java.util.Comparator;
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
public class AssistanceAgentPublishTrackingLogService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Claim claim;
		Integer id;
		TrackingLog trackingLog;

		if (!super.getRequest().getMethod().equals("POST") || super.getRequest().getMethod().equals("POST") && super.getRequest().hasData("masterId", Integer.class) || !super.getRequest().hasData("id", Integer.class))

			super.getResponse().setAuthorised(false);
		else {
			id = super.getRequest().getData("id", Integer.class);
			if (id == null) {
				super.getResponse().setAuthorised(false);
				return;
			}
			trackingLog = this.repository.findTrackingLogById(id);
			if (trackingLog != null) {
				claim = this.repository.findClaimByTrackingLogId(id);

				status = claim != null && !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

				int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
				status = status && assistanceAgentId == claim.getAssistanceAgent().getId();

				super.getResponse().setAuthorised(status);
			} else {
				super.getResponse().setAuthorised(false);
				return;
			}
			Date moment = MomentHelper.getCurrentMoment();
			Long maxComplete = this.repository.findNumberLatestTrackingLogByClaimFinish(claim.getId(), moment);
			if (maxComplete >= 2) {
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
		Date moment = MomentHelper.getCurrentMoment();

		List<TrackingLog> beforeActual = this.repository.findLatestTrackingLogByClaim(trackingLog.getClaim().getId(), trackingLog.getId(), moment);

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null && trackingLog.getResolution() != null) {
			Double percentage = trackingLog.getResolutionPercentage();
			TrackingLogStatus status = trackingLog.getStatus();
			Long countPercentage = this.repository.findNumberLatestTrackingLogByClaimNotFinishExceptHimself(trackingLog.getClaim().getId(), trackingLog.getId(), trackingLog.getResolutionPercentage());

			if (countPercentage > 0)
				super.state(false, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.notSamePercentage");

			boolean morePercentage = true;
			if (!beforeActual.isEmpty()) {
				beforeActual.sort(Comparator.comparing(TrackingLog::getResolutionPercentage).reversed());
				TrackingLog previous = beforeActual.get(0);

				TrackingLog oldTrackingLog = this.repository.findTrackingLogById(super.getRequest().getData("id", int.class));

				if (oldTrackingLog != null && !Objects.equals(oldTrackingLog.getResolutionPercentage(), trackingLog.getResolutionPercentage())) {
					morePercentage = trackingLog.getResolutionPercentage() > previous.getResolutionPercentage();

					super.state(morePercentage, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.wrongNewPercentage");
				}

				Long maxComplete = this.repository.findNumberLatestTrackingLogByClaimFinishExceptHimself(trackingLog.getClaim().getId(), trackingLog.getId(), moment);

				if (percentage.equals(100.00))
					if (maxComplete == 1) {
						super.state(status.equals(previous.getStatus()), "status", "assistanceAgent.trackingLog.form.error.statusNewPercentageFinished");
						super.state(!trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00), "*", "assistanceAgent.trackingLog.form.error.createTwoTrackingLogFinishedClaimPublished");
						super.state(!trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00) && !previous.isDraftMode(), "*", "assistanceAgent.trackingLog.form.error.createTwoTrackingLogFinishedTheBeforePublished");
					}
			}
			if (percentage.equals(100.00))
				for (TrackingLog t : beforeActual)
					if (t.isDraftMode()) {
						super.state(false, "*", "assistanceAgent.trackingLog.form.error.beforePublishingOtherPublished");
						break;
					}
		}

	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(false);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statuses;
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("statuses", statuses);
		dataset.put("claimId", trackingLog.getClaim().getId());
		dataset.put("draftMode", trackingLog.isDraftMode());
		dataset.put("claimDraftMode", trackingLog.getClaim().isDraftMode());

		super.getResponse().addData(dataset);
	}

}
