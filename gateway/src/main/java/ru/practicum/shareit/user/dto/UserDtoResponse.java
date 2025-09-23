package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
public class UserDtoResponse {
    private Long id;
    private String name;
    private String email;
}