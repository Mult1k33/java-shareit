package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.*;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemServerController {

    private final ItemService itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemFullDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех вещей пользователя c Id:{}", userId);
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemFullDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @PathVariable Long itemId) {
        log.info("Запрос на получение вещи с Id:{}", itemId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> searchByText(@RequestParam("text") String searchText) {
        log.info("Запрос на поиск вещи в названии или в описании");
        return itemService.searchByText(searchText);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemNewDto newItem) {
        log.info("Запрос на добавление новой вещи пользователем с Id:{}", userId);
        return itemService.create(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemNewDto updatedItem) {
        log.info("Запрос на обновление вещи с Id:{} пользователем с Id:{}", itemId, ownerId);
        return itemService.update(ownerId, itemId, updatedItem);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с Id:{} пользователем с Id:{}", itemId, ownerId);
        itemService.delete(ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentNewDto comment) {
        log.info("Запрос на добавление комментария вещи с Id:{} пользователем с Id:{}", itemId, userId);
        return itemService.addComment(userId, itemId, comment);
    }

    @GetMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentDto> findAllCommentsByItemId(@PathVariable Long itemId) {
        log.info("Запрос на получение всех комментариев к вещи с id:{}", itemId);
        return itemService.findAllCommentsByItemId(itemId);
    }
}