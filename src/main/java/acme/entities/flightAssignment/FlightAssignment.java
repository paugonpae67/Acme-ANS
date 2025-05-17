
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
import acme.constraints.ValidFlightAssignemnt;
import acme.constraints.ValidLongText;
import acme.entities.legs.Leg;
import acme.realms.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@ValidFlightAssignemnt
public class FlightAssignment extends AbstractEntity {

	private static final long		serialVersionUID	= 1L;

	@Optional
	@ValidLongText
	@Automapped
	private String					remarks;

	@Mandatory
	@ValidMoment(past = true)
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
	private FlightCrewMember		flightCrewMembers;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg						leg;

	@Mandatory
	@Automapped
	private boolean					draftMode;
}
