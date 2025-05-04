
package acme.features.assistanceAgent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claim.Claim;
import acme.realms.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentListFinishClaimService		listFinishService;

	@Autowired
	private AssistanceAgentShowClaimService				showService;

	@Autowired
	private AssistanceAgentListUndergoingClaimService	listUndergoingService;

	@Autowired
	private AssistanceAgentCreateClaimService			createService;

	@Autowired
	private AssistanceAgentUpdateClaimService			updateService;

	@Autowired
	private AssistanceAgentPublishClaimService			publishService;

	@Autowired
	private AssistanceAgentDeleteClaimService			deleteService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-finish", "list", this.listFinishService);
		super.addCustomCommand("list-undergoing", "list", this.listUndergoingService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addBasicCommand("delete", this.deleteService);
	}

}
