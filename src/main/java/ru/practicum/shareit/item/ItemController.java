package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemNewDto;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Collection<ItemFullDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех вещей пользователя c Id:{}", userId);
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemFullDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @PathVariable Long itemId) {
        log.info("Запрос на получение вещи с Id:{}", itemId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchByText(@RequestParam("text") String searchText) {
        log.info("Запрос на поиск вещи в названии или в описании");
        return itemService.searchByText(searchText);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemNewDto newItem) {
        log.info("Запрос на добавление новой вещи пользователем с Id:{}", userId);
        return itemService.create(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemNewDto updatedItem) {
        log.info("Запрос на обновление вещи с Id:{} пользователем с Id:{}", itemId, ownerId);
        return itemService.update(ownerId, itemId, updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с Id:{} пользователем с Id:{}", itemId, ownerId);
        itemService.delete(ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentNewDto comment) {
        log.info("Запрос на добавление комментария вещи с Id:{} пользователем с Id:{}", itemId, userId);
        return itemService.addComment(userId, itemId, comment);
    }

    @GetMapping("/{itemId}/comment")
    public Collection<CommentDto> findAllCommentsByItemId(@PathVariable Long itemId) {
        log.info("Запрос на получение всех комментариев к вещи с id:{}", itemId);
        return itemService.findAllCommentsByItemId(itemId);
    }
}