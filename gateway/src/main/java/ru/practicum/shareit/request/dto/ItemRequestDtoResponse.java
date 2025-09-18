package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Builder(toBuilder = true)
public class ItemRequestDtoResponse {
    private Long id;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private LocalDateTime created;

    private Collection<ItemDtoResponse> items;
}