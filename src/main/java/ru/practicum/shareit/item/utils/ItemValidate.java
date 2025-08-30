package ru.practicum.shareit.item.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemNewDto;

/**
 * Утилитарный класс для валидации сущности Item
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemValidate {

    // Валидация при создании вещи
    public static void validateForCreate(ItemNewDto itemNewDto) {
        validateNotNull(itemNewDto);
        validateName(itemNewDto.getName());
        validateDescription(itemNewDto.getDescription());
        validateAvailability(itemNewDto.getAvailable());
    }

    // Валидация при изменении вещи
    public static void validateForUpdate(ItemNewDto itemNewDto) {
        validateNotNull(itemNewDto);

        if (itemNewDto.getName() != null) {
            validateName(itemNewDto.getName());
        }

        if (itemNewDto.getDescription() != null) {
            validateDescription(itemNewDto.getDescription());
        }

        if (itemNewDto.getAvailable() != null) {
            validateAvailability(itemNewDto.getAvailable());
        }
    }

    // Вспомогательные методы для валидации полей Item
    private static void validateNotNull(ItemNewDto itemNewDto) {
        if (itemNewDto == null) {
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
}