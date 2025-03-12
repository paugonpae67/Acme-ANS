
package acme.entities.airports;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidIataCode;
import acme.constraints.ValidPhone;
import acme.constraints.ValidShortText;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Airport extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				name;

	@Mandatory
	@ValidIataCode
	@Column(unique = true)
	private String				iataCode;

	@Mandatory
	@Valid
	@Automapped
	private OperationalType		operationalScope;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				country;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				city;

	@Optional
	@ValidUrl
	@Automapped
	private String				website;

	@Optional
	@ValidEmail
	@Automapped
	private String				email;

	@Optional
	@ValidPhone
	@Automapped
	private String				phoneNumber;

}
