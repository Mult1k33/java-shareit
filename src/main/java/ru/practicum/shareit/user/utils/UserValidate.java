package ru.practicum.shareit.user.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

/**
 * Утилитарный класс для валидации данных сущности User
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserValidate {

    // Валидация при создании пользователя
    public static void validateForCreate(User user) {
        validateNotNull(user);
        validateEmail(user.getEmail());
    }

    // Валидация при изменении пользователя
    public static void validateForEdit(User user) {
        validateNotNull(user);

        if (user.getEmail() != null) {
            validateEmail(user.getEmail());
        }
    }

    // Вспомогательный метод валидации email
    private static void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            log.error("Попытка добавить пустой email");
            throw new ValidationException("email не может быть пустым");
        }
        if (!email.contains("@")) {
            log.error("Попытка добавить некорректный email");
            throw new ValidationException("email не содержит @");
        }
    }

    // Вспомогательный метод проверки пользователя на null
    private static void validateNotNull(User user) {
        if (user == null) {
            log.error("Попытка добавить или изменить null, а не пользователя");
            throw new ValidationException("Пользователь не может быть null");
        }
    }
}