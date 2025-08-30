package ru.practicum.shareit.booking.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;

import java.time.Instant;

/**
 * Утилитарный класс для валидации данных сущности Booking
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingValidate {

    // Валидация даты начала бронирования
    public static void validateForStartBooking(Booking booking) {
        validateNotNull(booking);
        if (booking.getStart().isBefore(Instant.now())) {
            log.warn("Задана некорректная дата начала бронирования");
            throw new ValidationException("Дата начала бронирования не может быть в прошлом");
        }
    }

    // Валидация даты окончания бронирования
    public static void validateForEndBooking(Booking booking) {
        validateNotNull(booking);
        if (booking.getEnd().isBefore(Instant.now())) {
            log.warn("Задана некорректная дата окончания бронирования");
            throw new ValidationException("Дата окончания бронирования не может быть в прошлом");
        }
    }

    // Валидация корректности временного интервала между началом и окончанием бронирования
    public static void validateTimeInterval(Booking booking) {
        validateNotNull(booking);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().equals(booking.getStart())) {
            log.warn("Задан некорректный интервал между началом и окончанием бронирования");
            throw new ValidationException("Дата окончания бронирования должна быть позже даты начала");
        }
    }

    // Комплексная валидация для Booking
    public static void validateAll(Booking booking) {
        validateNotNull(booking);
        validateForStartBooking(booking);
        validateForEndBooking(booking);
        validateTimeInterval(booking);
    }

    // Вспомогательный метод проверки бронирования на null
    private static void validateNotNull(Booking booking) {
        if (booking == null) {
            log.error("Бронирование не может быть null");
            throw new ValidationException("Бронирование не может быть null");
        }

        if (booking.getStart() == null || booking.getEnd() == null) {
            log.error("Некорректная дата начала или дата окончания бронирования");
            throw new ValidationException("Дата начала или дата окончания не может быть null");
        }
    }
}