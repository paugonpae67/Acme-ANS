
package acme.authenticated.assistanceAgent;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.realms.AssistanceAgent;

@GuiService
public class AuthenticatedAssistanceAgentCreateService extends AbstractGuiService<Authenticated, AssistanceAgent> {

	@Autowired
	private AuthenticatedAssistanceAgentRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AssistanceAgent agent;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		agent = new AssistanceAgent();
		agent.setUserAccount(userAccount);

		super.getBuffer().addData(agent);
	}

	@Override
	public void bind(final AssistanceAgent agent) {
		assert agent != null;

		super.bindObject(agent, "employeeCode", "spokenLanguages", "moment", "briefBio", "salary", "photo", "airline");
	}

	@Override
	public void validate(final AssistanceAgent agent) {
		String employeeCode = agent.getEmployeeCode();
		AssistanceAgent existing = this.repository.findAssistanceAgentByEmployeeCode(employeeCode);
		boolean valid = existing == null || existing.getId() == agent.getId();
		super.state(valid, "employeeCode", "assistanceAgent.validation.employeeCode.invalidEmployeeCode");
	}

	@Override
	public void perform(final AssistanceAgent object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final AssistanceAgent object) {
		Dataset dataset;
		Collection<Airline> airlines = this.repository.findAllAirlines();
		SelectChoices airlineChoices = SelectChoices.from(airlines, "iataCode", object.getAirline());

		dataset = super.unbindObject(object, "employeeCode", "spokenLanguages", "moment", "briefBio", "salary", "photo", "airline");
		dataset.put("airlineChoices", airlineChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
