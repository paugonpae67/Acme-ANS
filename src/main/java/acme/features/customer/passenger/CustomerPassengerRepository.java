
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.BookingRecord;
import acme.entities.passengers.Passenger;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("SELECT br.passenger FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<Passenger> findAllBookingPassengersByBookingId(int bookingId);

	@Query("SELECT p FROM Passenger p WHERE p.id = :passengerId")
	Passenger findPassengerById(int passengerId);

	@Query("SELECT p FROM Passenger p WHERE p.customer.id = :customerId")
	Collection<Passenger> findAllCustomerPassengersByCustomerId(int customerId);

	@Query("SELECT br FROM BookingRecord br WHERE br.passenger.id = :passengerId")
	Collection<BookingRecord> findPassengerBookingRecordsByPassengerId(int passengerId);

}
