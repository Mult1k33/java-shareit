package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDtoRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}