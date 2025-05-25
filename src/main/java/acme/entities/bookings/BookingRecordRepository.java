
package acme.entities.bookings;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface BookingRecordRepository extends AbstractRepository {

	@Query("SELECT COUNT(br) FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Integer countPassengersByBooking(Integer bookingId);

	@Query("SELECT br.passenger FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(Integer bookingId);
}
