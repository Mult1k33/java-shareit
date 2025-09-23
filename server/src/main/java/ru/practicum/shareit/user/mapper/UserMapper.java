package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    // Преобразование User в Dto
    public static UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    // Преобразование Dto в User
    public static User mapToUser(UserNewDto userNewDto) {
        return User.builder()
                .name(userNewDto.getName())
                .email(userNewDto.getEmail())
                .build();
    }
}