package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserNewDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll();

    UserDto findById(Long userId);

    UserDto create(UserNewDto newUser);

    UserDto update(Long userId, UserNewDto updatedUser);

    void delete(Long userId);
}