package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingDtoRequest bookingDtoRequest) {
        return post("", userId, bookingDtoRequest);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingByBooker(Long userId, BookingState state) {
        return get("?state={state}", userId, Map.of("state", state.name()));
    }

    public ResponseEntity<Object> getBookingByOwner(Long userId, BookingState state) {
        return get("/owner?state={state}", userId, Map.of("state", state.name()));
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        return patch("/" + bookingId + "?approved={approved}", userId,
                Map.of("approved", isApproved), "");
    }
}