package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersEmail = new HashSet<>();
    private long idCounter = 0L;

    @Override
    public Collection<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User create(User user) {
        user.setUserId(++idCounter);
        usersEmail.add(user.getEmail().toLowerCase());
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        usersEmail.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}