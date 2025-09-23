package ru.practicum.shareit.request.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestValidate {

    // Валидация при создании запроса
    public static void validateForCreate(ItemRequestNewDto itemRequestNewDto) {
        validateNotNull(itemRequestNewDto);
        validateDescription(itemRequestNewDto.getDescription());
    }

    // Вспомогательные методы для валидации полей ItemRequest
    private static void validateNotNull(ItemRequestNewDto itemRequestNewDto) {
        if (itemRequestNewDto == null) {
            log.error("Попытка создать null, а не запрос");
            throw new ValidationException("Запрос не может быть null");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            log.error("Попытка создать запрос без описания");
            throw new ValidationException("Описание запроса не может быть пустым");
        }
    }
}