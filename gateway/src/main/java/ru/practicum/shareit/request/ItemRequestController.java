package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> create(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestBody @Valid ItemRequestDtoRequest itemRequestDtoRequest) {
        log.info("POST / requests | userId: {} | Создание заявки: '{}'",
                userId, itemRequestDtoRequest.getDescription());
        return itemRequestClient.createRequest(userId, itemRequestDtoRequest);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoResponse>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("GET / requests");
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoResponse>> getRequestByUser(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("GET / requests / requestor {}", userId);
        return itemRequestClient.getRequestByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoResponse> getRequestById(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @PathVariable @Positive Long requestId) {
        log.info("GET / request {} / user {}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}