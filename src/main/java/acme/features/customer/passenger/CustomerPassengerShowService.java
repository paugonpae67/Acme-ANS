
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerShowService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {

		if (!super.getRequest().getMethod().equals("GET"))
			super.getResponse().setAuthorised(false);

		else if (super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("id", int.class))
			super.getResponse().setAuthorised(false);

		else {
			boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
			super.getResponse().setAuthorised(status);

			int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			Integer passengerId = super.getRequest().getData("id", Integer.class);
			if (passengerId == null)
				super.getResponse().setAuthorised(false);
			else {
				Passenger passenger = this.repository.findPassengerById(passengerId);
				super.getResponse().setAuthorised(passenger != null && customerId == passenger.getCustomer().getId());
			}
		}
	}

	@Override
	public void load() {
		Passenger passenger;
		int id = super.getRequest().getData("id", int.class);

		passenger = this.repository.findPassengerById(id);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "draftMode", "customer");
		super.getResponse().addData(dataset);
	}
}
