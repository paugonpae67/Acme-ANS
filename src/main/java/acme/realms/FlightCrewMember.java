
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidPhone;
import acme.constraints.ValidUserIdentifier;
import acme.entities.airlines.Airline;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidUserIdentifier
@Entity
public class FlightCrewMember extends AbstractRole {

	private static final long		serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String					employeeCode;

	@Mandatory
	@ValidPhone
	@Automapped
	private String					phoneNumber;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String					languageSkills;

	@Mandatory
	@ValidMoney
	@Automapped
	private Money					salary;

	@Optional
	@ValidNumber(min = 0, max = 120)
	@Automapped
	private Integer					yearsOfExperience;

	@Mandatory
	@Valid
	@Automapped
	private FlightCrewMemberStatus	availabilityStatus;

	//Relationships

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline					airline;

}
