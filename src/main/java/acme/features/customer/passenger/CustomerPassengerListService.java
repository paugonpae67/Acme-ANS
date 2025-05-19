
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		try {
			if (!super.getRequest().getMethod().equals("GET"))
				super.getResponse().setAuthorised(false);
			else if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
				super.getResponse().setAuthorised(false);
			else {
				boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
				super.getResponse().setAuthorised(status);
			}
		} catch (Throwable t) {
			super.getResponse().setAuthorised(false);
		}

	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		passengers = this.repository.findAllCustomerPassengersByCustomerId(customerId);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber");

		super.getResponse().addData(dataset);
	}

}
