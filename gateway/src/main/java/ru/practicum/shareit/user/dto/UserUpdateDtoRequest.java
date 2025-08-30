package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDtoRequest {

    private String name;

    @Email
    private String email;
}