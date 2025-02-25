
package acme.entities.airlines;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import acme.client.components.basis.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Airline extends AbstractEntity {

	@NotBlank
	@Size(max = 50)
	private String		name;

	@NotBlank
	@Pattern(regexp = "^[A-Z]{2}X$")
	@Column(unique = true)
	private String		iataCode;

	@NotBlank
	private String		website;

	@NotBlank
	@Pattern(regexp = "LUXURY|STANDARD|LOW-COST")
	private String		type;

	@Past
	private LocalDate	foundationMoment;

	@Email
	private String		email;

	@Pattern(regexp = "^\\+?\\d{6,15}$")
	private String		phoneNumber;
}
