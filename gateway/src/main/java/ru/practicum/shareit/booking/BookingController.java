package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<BookingDtoResponse> create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                     @RequestBody @Valid BookingDtoRequest bookingDtoRequest) {
        log.info("POST / bookings");
        return bookingClient.createBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> approve(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                      @PathVariable @Positive Long bookingId,
                                                      @RequestParam(name = "approved") Boolean isApproved) {
        log.info("PATCH / bookings / {}", bookingId);
        return bookingClient.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> getById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                      @PathVariable @Positive Long bookingId) {
        log.info("GET booking {}, userId = {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoResponse>> getByOwnerId(
            @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState bookingState) {
        log.info("GET / ByOwner / {}", ownerId);
        return bookingClient.getBookingByOwner(ownerId, bookingState);
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoResponse>> getByBookerId(
            @RequestHeader("X-Sharer-User-Id") @Positive Long bookerId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState bookingState) {
        log.info("GET / ByBooker / {}", bookerId);
        return bookingClient.getBookingByBooker(bookerId, bookingState);
    }
}