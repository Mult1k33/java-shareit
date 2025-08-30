package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET / users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getByUserId(@PathVariable @Positive Long userId) {
        log.info("GET / users / {}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDtoRequest userDtoRequest) {
        log.info("POST / users / {} / {}", userDtoRequest.getName(), userDtoRequest.getEmail());
        return userClient.createUser(userDtoRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @Positive Long userId,
                                         @RequestBody @Valid UserUpdateDtoRequest userUpdateDtoRequest) {
        log.info("PATCH / users / {}", userId);
        return userClient.updateUser(userId, userUpdateDtoRequest);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable @Positive Long userId) {
        log.info("DELETE / users / {}", userId);
        userClient.deleteUser(userId);
    }
}