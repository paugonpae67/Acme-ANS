
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidLicenseNumber;
import acme.constraints.ValidPhone;
import acme.constraints.ValidShortText;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(indexes = {
	@Index(columnList = "user_account_id")
})
public class Technician extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidLicenseNumber
	@Column(unique = true)
	private String				licenseNumber;

	@Mandatory
	@ValidPhone
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				specialization;

	@Mandatory
	@Automapped
	private boolean				healthTestPassed;

	@Mandatory
	@ValidNumber(min = 0, max = 120)
	@Automapped
	private Integer				yearsOfExperience;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				certifications;
}
