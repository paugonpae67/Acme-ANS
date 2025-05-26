
package acme.entities.claim;

import java.beans.Transient;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidClaims;
import acme.constraints.ValidLongText;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogRepository;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@ValidClaims
@Table(indexes = {
	@Index(columnList = "id"), @Index(columnList = "assistance_agent_id")
})
public class Claim extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private ClaimType			type;

	@Mandatory
	@Automapped
	private boolean				draftMode;


	@Transient
	public TrackingLogStatus getStatus() {
		TrackingLogRepository repository = SpringHelper.getBean(TrackingLogRepository.class);
		Optional<List<TrackingLog>> optionalList = repository.findLatestTrackingLogByClaim(this.getId());

		if (optionalList.isPresent())
			return optionalList.get().stream().sorted(Comparator.comparing(TrackingLog::getLastUpdateMoment).reversed().thenComparing(TrackingLog::getId, Comparator.reverseOrder())).findFirst().map(TrackingLog::getStatus).orElse(TrackingLogStatus.PENDING);

		return TrackingLogStatus.PENDING;
	}


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AssistanceAgent	assistanceAgent;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg				leg;
}
