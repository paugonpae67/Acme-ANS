
package acme.entities.bookings;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidLongText;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
/*
 * @Table(indexes = {
 * 
 * @Index(columnList = "customer_id")
 * })
 */
public class Passenger extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				fullName;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				email;

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,9}$")
	@Automapped
	private String				passportNumber;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				dateOfBirth;

	@Optional
	@ValidString(min = 0, max = 50)
	@Automapped
	private String				specialNeeds;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	//Relationships ---------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Customer			customer;

}
