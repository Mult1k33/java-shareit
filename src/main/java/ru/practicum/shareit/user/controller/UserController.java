package ru.practicum.shareit.user.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@NonNull @PathVariable("id") Long userId) {
        log.info("Запрос на получение пользователя с Id:{}", userId);
        return userService.findById(userId);
    }

    @PostMapping
    public UserDto create(@NonNull @RequestBody UserDto newUser) {
        log.info("Запрос на добавление нового пользователя");
        return userService.create(newUser);
    }

    @PatchMapping("/{id}")
    public UserDto update(@NonNull @PathVariable("id") Long userId,
                          @NonNull @RequestBody UserDto updatedUser) {
        log.info("Запрос на обновление пользователя с id:{}", updatedUser.getId());
        return userService.update(userId, updatedUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@NonNull @PathVariable("id") Long userId) {
        log.info("Запрос на удаление пользователя с id:{}", userId);
        userService.delete(userId);
    }
}