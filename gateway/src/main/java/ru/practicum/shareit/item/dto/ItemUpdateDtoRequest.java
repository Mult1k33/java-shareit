package ru.practicum.shareit.item.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDtoRequest {

    @Nullable
    private String name;

    @Nullable
    private String description;

    @Nullable
    private Boolean available;

    @Nullable
    @Positive
    private Long requestId;
}