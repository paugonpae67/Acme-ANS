
package acme.features.assistanceAgent.trakingLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiController
public class AssistanceAgentTrackingLogController extends AbstractGuiController<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentListTrackingLogService		listService;

	@Autowired
	private AssistanceAgentShowTrackingLogSerivce		showService;

	@Autowired
	private AssistanceAgentCreateTrackingLogService		createService;

	@Autowired
	private AssistanceAgentPublishTrackingLogService	publishService;

	@Autowired
	private AssistanceAgentUpdateTrackingLogService		updateService;

	@Autowired
	private AssistanceAgentDeleteTrackingLogService		deleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
