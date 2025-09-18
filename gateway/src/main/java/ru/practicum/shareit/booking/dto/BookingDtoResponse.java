package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
public class BookingDtoResponse {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDtoResponse item;

    private UserDtoResponse booker;

    private String status;
}