
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRecord;
import acme.entities.bookings.Passenger;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.id = :bookingId")
	Booking findBookingById(int bookingId);

	@Query("SELECT p FROM Passenger p WHERE p.id = :passengerId")
	Passenger findPassengerById(int passengerId);

	@Query("SELECT br.passenger FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("SELECT br FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordByBookingId(int bookingId);

	@Query("SELECT br FROM BookingRecord br WHERE br.id = :bookingRecordId")
	BookingRecord findBookingRecordById(int bookingRecordId);

	@Query("SELECT p FROM Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("SELECT p FROM Passenger p WHERE p.customer.id = :customerId")
	Collection<Passenger> findAllCustomerPassengersByCustomerId(int customerId);

	@Query("SELECT br.booking FROM BookingRecord br WHERE br.id = :bookingRecordId")
	Booking findBookingByBookingRecordId(int bookingRecordId);

	@Query("SELECT br from BookingRecord br WHERE br.passenger.id= :passengerId and br.booking.id= :bookingId")
	BookingRecord findBookingRecordByPassengerIdAndBookingId(int passengerId, int bookingId);

}
