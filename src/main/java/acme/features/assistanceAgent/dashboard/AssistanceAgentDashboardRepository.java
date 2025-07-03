
package acme.features.assistanceAgent.dashboard;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.realms.AssistanceAgent;

@Repository
public interface AssistanceAgentDashboardRepository extends AbstractRepository {

	@Query("select c from Claim c")
	Collection<Claim> findAllClaim();

	@Query("select c from Claim c where c.registrationMoment is not null ")
	Collection<Claim> findClaimsOfAMonth();

	@Query("select count(t) from TrackingLog t where t.claim.id = :claimId")
	Long countLogsByClaimId(int claimId);

	@Query("select c from Claim c where c.registrationMoment > :fecha")
	List<Claim> findClaimsAfterDate(Date fecha);

	@Query("select a from AssistanceAgent a")
	Collection<AssistanceAgent> findAllAssistanceAgent();

}
