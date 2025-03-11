
package acme.entities.bookingRecords;

import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;

public class BookingRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	//Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Booking				booking;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Passenger			passenger;

}
