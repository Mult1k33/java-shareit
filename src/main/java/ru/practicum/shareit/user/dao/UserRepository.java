package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> findAll();

    Optional<User> findById(Long userId);

    User create(User user);

    User update(Long userId, User user);

    void delete(Long userId);
}