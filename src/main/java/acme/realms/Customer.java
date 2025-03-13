
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidLongText;
import acme.constraints.ValidPhone;
import acme.constraints.ValidShortText;
import acme.constraints.ValidUserIdentifier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidUserIdentifier
@Entity
public class Customer extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String				identifier;

	@Mandatory
	@ValidPhone
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				physicalAddress;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				city;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				country;

	@Optional
	@ValidNumber(min = 0, max = 500000)
	@Automapped
	private Integer				earnedPoints;

}
