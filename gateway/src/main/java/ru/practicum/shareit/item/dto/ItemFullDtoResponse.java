package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
public class ItemFullDtoResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private BookingDtoResponse lastBooking;
    private BookingDtoResponse nextBooking;
    private List<CommentDtoResponse> comments;
}