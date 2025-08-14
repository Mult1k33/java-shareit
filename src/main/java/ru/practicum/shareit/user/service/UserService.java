package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll();

    UserDto findById(Long userId);

    UserDto create(UserDto newUser);

    UserDto update(Long userId, UserDto updatedUser);

    void delete(Long userId);
}