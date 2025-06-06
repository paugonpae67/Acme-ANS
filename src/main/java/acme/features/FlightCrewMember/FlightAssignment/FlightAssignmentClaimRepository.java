
package acme.features.FlightCrewMember.FlightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.FlightCrewMember;

@Repository
public interface FlightAssignmentClaimRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.id = :id ")
	FlightAssignment findAssignmentById(int id);

	@Query("select f from FlightAssignment f where f.flightCrewMembers.id = :id AND f.leg.scheduledArrival > :date ")
	Collection<FlightAssignment> findFlightAssignmentInFuture(int id, Date date);

	@Query("select f from FlightAssignment f where f.flightCrewMembers.id = :id AND f.leg.scheduledArrival <= :date")
	Collection<FlightAssignment> findFlightAssignmentInPast(int id, Date date);

	@Query("select f from FlightCrewMember f where f.id = :id ")
	FlightCrewMember findFlightCrewMemberById(int id);

	@Query("select f from Leg f where f.id = :id ")
	Leg findLegById(int id);

	@Query("SELECT m FROM FlightCrewMember m WHERE m.availabilityStatus = 'AVAILABLE'")
	Collection<FlightCrewMember> findAllAvailableMembers();

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select l from Leg l where l.scheduledArrival > :date AND l.scheduledDeparture > :date ")
	Collection<Leg> findAllLegsFuture(Date date);

	@Query("select l from Leg l where l.scheduledArrival > :date AND l.scheduledDeparture > :date AND l.draftMode = false")
	Collection<Leg> findAllLegsFuturePublished(Date date);

	@Query("select DISTINCT l.leg from FlightAssignment l where l.flightCrewMembers.id = :id AND l.id != :assignemnet")
	Collection<Leg> findLegsByFlightCrewMember(int id, int assignemnet);

	@Query("SELECT DISTINCT fa.leg FROM FlightAssignment fa WHERE fa.flightCrewMembers.id = :memberId")
	Collection<Leg> findLegsByMemberId(int memberId);

	@Query("select l from ActivityLog l where l.flightAssignment.id = :id")
	Collection<ActivityLog> getActivityLogByFlight(int id);

	@Query("select l from FlightAssignment l where l.leg.id = :id")
	Collection<FlightAssignment> findFlightAssignmentByLegId(int id);

	@Query("select m from FlightCrewMember m where m.employeeCode = :employeeCode")
	FlightCrewMember findMemberByEmployeeCode(String employeeCode);
}
