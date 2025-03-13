
package acme.entities.systemCurrency;

import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidCurrency;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidSystemCurrency;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidSystemCurrency
public class SystemCurrency extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidCurrency
	@Automapped
	private String				currency;

	@Mandatory
	@ValidString(pattern = "^(?:[A-Z]{3})(?:,[A-Z]{3})*$")
	@Automapped
	private String				acceptedCurrencies;
}
