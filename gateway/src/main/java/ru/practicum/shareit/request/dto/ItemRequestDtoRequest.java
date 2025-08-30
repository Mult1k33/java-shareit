package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoRequest {

    @NotBlank
    private String description;
}