package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserNewDto;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя с Id:{}", userId);
        return userService.findById(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserNewDto newUser) {
        log.info("Запрос на добавление нового пользователя");
        return userService.create(newUser);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UserNewDto updatedUser) {
        log.info("Запрос на обновление пользователя с id:{}", userId);
        return userService.update(userId, updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя с id:{}", userId);
        userService.delete(userId);
    }
}