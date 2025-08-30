package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED'")
    Collection<Booking> findByBookerIdStateCurrent(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED'")
    Collection<Booking> findByBookerIdStatePast(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED'")
    Collection<Booking> findByBookerIdStateFuture(Long bookerId);

    Collection<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status);

    Collection<Booking> findByItemOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED'")
    Collection<Booking> findByOwnerIdStateCurrent(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED'")
    Collection<Booking> findByOwnerIdStatePast(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED'")
    Collection<Booking> findByOwnerIdStateFuture(Long ownerId);

    Collection<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.item.owner.id = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    Collection<Booking> findByOwnerIdLastBookingsForItem(Long itemId, Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.item.owner.id = :ownerId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start ASC")
    Collection<Booking> findByOwnerIdNextBookingsForItem(Long itemId, Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.end <= CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.end DESC")
    Collection<Booking> findByBookerIdLastBookingsForItem(Long bookerId, Long itemId);
}