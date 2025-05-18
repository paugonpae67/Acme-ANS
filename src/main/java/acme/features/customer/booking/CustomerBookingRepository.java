
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRecord;
import acme.entities.bookings.Passenger;
import acme.entities.flights.Flight;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId")
	Collection<Booking> findAllBookingsByCustomerId(int customerId);

	@Query("SELECT b FROM Booking b WHERE b.id = :id")
	Booking findBookingById(int id);

	@Query("SELECT b FROM Booking b WHERE b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(String locatorCode);

	@Query("SELECT f FROM Flight f")
	Collection<Flight> findAllFlights();

	@Query("SELECT br.passenger FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("SELECT br FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingBookingRecordsByBookingId(int bookingId);

}
