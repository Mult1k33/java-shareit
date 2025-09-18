package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<List<ItemDtoResponse>> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("GET / items / user {}", userId);
        return itemClient.getAllItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemFullDtoResponse> getItemById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                           @PathVariable @Positive Long itemId) {
        log.info("GET / items {} / user {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDtoResponse>> searchByText(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestParam("text") @NotBlank String searchText) {
        log.info("GET / search / {}", searchText);
        return itemClient.searchByText(userId, searchText);
    }

    @PostMapping
    public ResponseEntity<ItemDtoResponse> create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                  @RequestBody @Valid ItemDtoRequest itemDtoRequest) {
        log.info("POST / items {} / user {}", itemDtoRequest.getName(), userId);
        return itemClient.createItem(userId, itemDtoRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> update(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                                  @PathVariable @Positive Long itemId,
                                                  @RequestBody @Valid ItemUpdateDtoRequest itemUpdateDtoRequest) {
        log.info("PATCH / items {} / user {}", itemId, ownerId);
        return itemClient.updateItem(ownerId, itemId, itemUpdateDtoRequest);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                       @PathVariable @Positive Long itemId) {
        log.info("DELETE / items {} / user {}", itemId, ownerId);
        return itemClient.deleteItem(ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDtoResponse> addComment(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                         @PathVariable @Positive Long itemId,
                                                         @RequestBody @Valid CommentDtoRequest commentDtoRequest) {
        log.info("POST / comment / item {}", itemId);
        return itemClient.addComment(userId, itemId, commentDtoRequest);
    }

    @GetMapping("/{itemId}/comment")
    public ResponseEntity<List<CommentDtoResponse>> getComments(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @PathVariable @Positive Long itemId) {
        log.info("GET / comment / item {}", itemId);
        return itemClient.getAllComments(userId, itemId);
    }
}