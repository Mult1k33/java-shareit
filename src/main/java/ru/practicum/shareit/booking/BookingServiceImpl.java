package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.utils.BookingValidate;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    // Получение бронирования по id
    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Некорректный id пользователя");
        }

        if (bookingId == null || bookingId <= 0) {
            throw new ValidationException("Некорректный id бронирования");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        // Проверяем права доступа
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Доступ к бронированию запрещен");
        }

        return BookingMapper.mapToDto(booking);
    }

    // Добавление бронирования
    @Override
    public BookingDto create(Long userId, BookingNewDto bookingNewDto) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Некорректный ID пользователя");
        }

        if (bookingNewDto == null) {
            throw new ValidationException("Бронирование не может быть null");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingNewDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id:" + bookingNewDto.getItemId() + "не найдена"));

        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Владелец не может бронировать свою вещь");
        }

        // Проверяем доступность вещи
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        Booking booking = BookingMapper.mapToBooking(bookingNewDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        BookingValidate.validateAll(booking);

        Booking createdBooking = bookingRepository.save(booking);
        return BookingMapper.mapToDto(createdBooking);
    }

    // Подтверждение бронирования
    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean isApproved) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Некорректный ID пользователя");
        }

        if (bookingId == null || bookingId <= 0) {
            throw new ValidationException("Некорректный ID бронирования");
        }

        if (isApproved == null) {
            throw new ValidationException("Статус подтверждения не может быть null");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        // Проверка прав доступа
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Пользователь ID: {} не является владельцем вещи ID: {}",
                    userId, booking.getItem().getId());
            throw new ForbiddenException("Только владелец вещи может подтверждать бронирование");
        }

        // Проверка текущего статуса
        if (!BookingStatus.WAITING.equals(booking.getStatus())) {
            log.warn("Попытка изменить статус бронирования Id: {} с текущим статусом: {}",
                    bookingId, booking.getStatus());
            throw new ValidationException("Бронирование уже обработано");
        }

        // Установка нового статуса
        BookingStatus newStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Бронирование Id: {} обновлено со статусом: {}", bookingId, newStatus);

        return BookingMapper.mapToDto(updatedBooking);
    }

    // Получение списка бронирований владельца
    @Override
    public Collection<BookingDto> findByOwnerId(Long ownerId, BookingState bookingState) {
        if (ownerId == null || ownerId <= 0) {
            throw new ValidationException("Некорректный ID пользователя");
        }

        if (bookingState == null) {
            throw new ValidationException("Статус бронирования не может быть null");
        }

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + ownerId + " не найден"));

        Collection<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerId(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByOwnerIdStateCurrent(ownerId);
                break;
            case PAST:
                bookings = bookingRepository.findByOwnerIdStatePast(ownerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findByOwnerIdStateFuture(ownerId);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Неизвестное состояние бронирования");
        }

        return bookings.stream()
                .map(BookingMapper::mapToDto)
                .collect(Collectors.toList());
    }

    // Получение списка бронирований пользователя
    @Override
    public Collection<BookingDto> findByBookerId(Long bookerId, BookingState bookingState) {
        if (bookerId == null || bookerId <= 0) {
            throw new ValidationException("Некорректный ID пользователя");
        }

        if (bookingState == null) {
            throw new ValidationException("Статус бронирования не может быть null");
        }

        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + bookerId + " не найден"));

        Collection<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByBookerId(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdStateCurrent(bookerId);
                break;
            case PAST:
                bookings = bookingRepository.findByOwnerIdStatePast(bookerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdStateFuture(bookerId);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Неизвестное состояние бронирования");
        }

        return bookings.stream()
                .map(BookingMapper::mapToDto)
                .collect(Collectors.toList());
    }
}