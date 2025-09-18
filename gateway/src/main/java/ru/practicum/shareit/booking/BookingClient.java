package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.client.BaseClient;

import java.util.List;
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

    public ResponseEntity<BookingDtoResponse> createBooking(Long userId, BookingDtoRequest bookingDtoRequest) {
        return post("", userId, bookingDtoRequest, BookingDtoResponse.class);
    }

    public ResponseEntity<BookingDtoResponse> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId, BookingDtoResponse.class);
    }

    public ResponseEntity<List<BookingDtoResponse>> getBookingByBooker(Long userId, BookingState state) {
        ParameterizedTypeReference<List<BookingDtoResponse>> typeRef =
                new ParameterizedTypeReference<List<BookingDtoResponse>>() {};
        return get("?state={state}", userId, Map.of("state", state.name()), typeRef);
    }

    public ResponseEntity<List<BookingDtoResponse>> getBookingByOwner(Long userId, BookingState state) {
        ParameterizedTypeReference<List<BookingDtoResponse>> typeRef =
                new ParameterizedTypeReference<List<BookingDtoResponse>>() {};
        return get("/owner?state={state}", userId, Map.of("state", state.name()), typeRef);
    }

    public ResponseEntity<BookingDtoResponse> approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        return patch("/" + bookingId + "?approved={approved}", userId,
                Map.of("approved", isApproved), "", BookingDtoResponse.class);
    }
}