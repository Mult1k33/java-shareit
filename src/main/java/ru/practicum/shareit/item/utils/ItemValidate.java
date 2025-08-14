package ru.practicum.shareit.item.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

/**
 * Утилитарный класс для валидации сущности Item
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemValidate {

    // Валидация при создании вещи
    public static void validateForCreate(Item item) {
        validateNotNull(item);
        validateName(item.getName());
        validateDescription(item.getDescription());
        validateAvailability(item.getAvailable());
        validateOwner(item.getOwnerId());
    }

    // Валидация при изменении вещи
    public static void validateForUpdate(Item item) {
        validateNotNull(item);

        if (item.getName() != null) {
            validateName(item.getName());
        }

        if (item.getDescription() != null) {
            validateDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            validateAvailability(item.getAvailable());
        }
    }

    // Вспомогательные методы для валидации полей Item
    private static void validateNotNull(Item item) {
        if (item == null) {
            log.error("Попытка добавить или изменить null, а не вещь");
            throw new ValidationException("Вещь не может быть null");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            log.error("Попытка создать вещь без названия");
            throw new ValidationException("Название вещи не может быть пустым");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            log.error("Попытка добавить вещь без описания");
            throw new ValidationException("Описание вещи не может быть пустым");
        }
    }

    private static void validateAvailability(Boolean available) {
        if (available == null) {
            log.error("Попытка добавить вещь без статуса доступности");
            throw new ValidationException("Статус доступности вещи должен быть указан");
        }
    }

    private static void validateOwner(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            log.error("Попытка добавить вещь с некорректным Id владельца");
            throw new ValidationException("Некорректный Id владельца");
        }
    }
}