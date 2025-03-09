
package acme.entities.flightAssignment;

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
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.aircrafts.Aircraft;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FlightAssignment extends AbstractEntity {

	private static final long		serialVersionUID	= 1L;

	@Optional
	@ValidString(min = 1, max = 255)
	@Automapped
	private String					remarks;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date					moment;

	@Mandatory
	@Valid
	@Automapped
	private FlightAssignmentStatus	currentStatus;

	@Mandatory
	@Valid
	@Automapped
	private FlightAssignmentDuty	duty;

	//Relationships

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft				aircraft;

	/*
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @ManyToOne(optional = false)
	 * private Leg leg;
	 * 
	 */

}
