
package acme.authenticated.assistanceAgent;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airlines.Airline;
import acme.realms.AssistanceAgent;

@Repository
public interface AuthenticatedAssistanceAgentRepository extends AbstractRepository {

	@Query("select user from UserAccount user where user.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select a from AssistanceAgent a where a.employeeCode = :employeeCode")
	AssistanceAgent findAssistanceAgentByEmployeeCode(String employeeCode);

	@Query("select a from Airline a")
	Collection<Airline> findAllAirlines();

	@Query("select a from AssistanceAgent a where a.userAccount.id = :id")
	AssistanceAgent findAssistanceAgentByUserAccountId(int id);
}
