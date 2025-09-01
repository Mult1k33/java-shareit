package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long bookingId) {
        log.info("Запрос на получение бронирования с Id:{} от пользователя с Id:{}", bookingId, userId);
        return bookingService.findById(userId, bookingId);
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody BookingNewDto bookingNewDto) {
        log.info("Запрос на создание бронирования с Id:{} от пользователя с Id:{}", userId, bookingNewDto.getItemId());
        return bookingService.create(userId, bookingNewDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam(name = "approved") Boolean isApproved) {
        log.info("Запрос на подтверждение или отклонение бронирования с Id:{}", bookingId);
        return bookingService.approve(userId, bookingId, isApproved);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(name = "state", defaultValue = "ALL")
                                                BookingState bookingState) {
        log.info("Запрос на получение списка бронирований владельца с id:{}", ownerId);
        return bookingService.findByOwnerId(ownerId, bookingState);
    }

    @GetMapping
    public Collection<BookingDto> findByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                 @RequestParam(name = "state", defaultValue = "ALL")
                                                 BookingState bookingState) {
        log.info("Запрос на получение списка бронирований пользователя с id:{}", bookerId);
        return bookingService.findByBookerId(bookerId, bookingState);
    }
}