
package acme.entities.review;

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Review extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				namePerson;

	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	private Date				moment;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				subject;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				text;

	@Optional
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private double				score;

	@Mandatory
	@Valid
	@Automapped
	private boolean				recommended;

	//Relationships

	/*
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @ManyToOne(optional = false)
	 * private Service service;
	 * 
	 * 
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @ManyToOne(optional = false)
	 * private Flight flight;
	 * 
	 * 
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @ManyToOne(optional = false)
	 * private Airline airline;
	 */

}
