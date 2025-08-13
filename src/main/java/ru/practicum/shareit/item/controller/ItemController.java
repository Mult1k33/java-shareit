package ru.practicum.shareit.item.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> findAllByUserId(@NonNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех вещей пользователя c Id:{}", userId);
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@NonNull @PathVariable Long itemId) {
        log.info("Запрос на получение вещи с Id:{}", itemId);
        return itemService.findById(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchByText(@NonNull @RequestParam("text") String searchText) {
        log.info("Запрос на поиск вещи в названии или в описании");
        return itemService.searchByText(searchText);
    }

    @PostMapping
    public ItemDto create(@NonNull @RequestHeader("X-Sharer-User-Id") Long userId,
                          @NonNull @RequestBody ItemDto newItem) {
        log.info("Запрос на добавление новой вещи пользователем с Id:{}", userId);
        return itemService.create(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@NonNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @NonNull @PathVariable Long itemId,
                          @NonNull @RequestBody ItemDto updatedItem) {
        log.info("Запрос на обновление вещи с Id:{} пользователем с Id:{}", itemId, ownerId);
        return itemService.update(ownerId, itemId, updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@NonNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @NonNull @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с Id:{} пользователем с Id:{}", itemId, ownerId);
        itemService.delete(ownerId, itemId);
    }
}