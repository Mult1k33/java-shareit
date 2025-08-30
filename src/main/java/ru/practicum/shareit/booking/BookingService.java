package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingDto findById(Long userId, Long bookingId);

    BookingDto create(Long userId, BookingNewDto bookingNewDto);

    BookingDto approve(Long userId, Long bookingId, Boolean isApproved);

    Collection<BookingDto> findByOwnerId(Long ownerId, BookingState bookingState);

    Collection<BookingDto> findByBookerId(Long bookerId, BookingState bookingState);
}