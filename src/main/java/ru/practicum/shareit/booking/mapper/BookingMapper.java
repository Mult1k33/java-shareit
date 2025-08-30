package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    // Преобразование Booking в DTO
    public static BookingDto mapToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapToDto(booking.getItem()))
                .booker(UserMapper.mapToDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    // Преобразование BookingNewDto в Booking
    public static Booking mapToBooking(BookingNewDto bookingNewDto) {
        return Booking.builder()
                .start(bookingNewDto.getStart())
                .end(bookingNewDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }
}