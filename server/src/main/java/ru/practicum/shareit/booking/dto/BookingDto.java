package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;

@Getter
@Setter
@Builder(toBuilder = true)
public class BookingDto {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private Instant start;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private Instant end;

    private ItemDto item;

    private UserDto booker;

    private BookingStatus status;
}