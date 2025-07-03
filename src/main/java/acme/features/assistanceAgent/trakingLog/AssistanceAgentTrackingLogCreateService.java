
package acme.features.assistanceAgent.trakingLog;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

		if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", Integer.class)) {
			super.getResponse().setAuthorised(false);
			return;
		} else if (super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("masterId", Integer.class)) {
			super.getResponse().setAuthorised(false);
			return;
		} else {
			if (super.getRequest().getMethod().equals("POST")) {

				if (super.getRequest().getData("id", Integer.class) == null) {
					super.getResponse().setAuthorised(false);
					return;
				}
				int id = super.getRequest().getData("id", Integer.class);
				if (id != 0) {
					super.getResponse().setAuthorised(false);
					return;
				}
			}

			if (super.getRequest().getMethod().equals("GET") && super.getRequest().getData("masterId", Integer.class) == null) {
				super.getResponse().setAuthorised(false);
				return;
			}

			claimId = super.getRequest().getData("masterId", Integer.class);
			claim = this.repository.findClaimById(claimId);
			if (claim == null) {
				super.getResponse().setAuthorised(false);
				return;
			}
			status = claim != null && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

			int agentId = super.getRequest().getPrincipal().getActiveRealm().getId();

			status = status && agentId == claim.getAssistanceAgent().getId();

			Date moment = MomentHelper.getCurrentMoment();
			Long maxComplete = this.repository.findNumberLatestTrackingLogByClaimFinish(claim.getId(), moment);
			if (maxComplete >= 2) {
				super.getResponse().setAuthorised(false);
				return;
			}

			super.getResponse().setAuthorised(status);
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

				Long maxComplete = this.repository.findNumberLatestTrackingLogByClaimFinishExceptHimself(trackingLog.getClaim().getId(), trackingLog.getId(), moment);

				if (percentage.equals(100.00))
					if (maxComplete == 1) {
						super.state(status.equals(previous.getStatus()), "status", "assistanceAgent.trackingLog.form.error.statusNewPercentageFinished");
						super.state(!trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00), "*", "assistanceAgent.trackingLog.form.error.createTwoTrackingLogFinishedClaimPublished");
						super.state(!trackingLog.getClaim().isDraftMode() && previous.getResolutionPercentage().equals(100.00) && !previous.isDraftMode(), "*", "assistanceAgent.trackingLog.form.error.createTwoTrackingLogFinishedTheBeforePublished");
					}
				if (!percentage.equals(100.00)) {
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
