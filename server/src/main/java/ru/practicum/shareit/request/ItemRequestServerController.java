package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestServerController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestNewDto itemRequestNewDto) {
        log.info("Запрос на создание нового запроса пользователем с Id:{}", userId);
        return itemRequestService.create(userId, itemRequestNewDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех запросов пользователя с Id:{}", userId);
        return itemRequestService.findAllByUserId(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> findAllOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех запросов для пользователя с Id:{} от других пользователей", userId);
        return itemRequestService.findAllOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto findRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        log.info("Запрос на получение запроса с Id:{} пользователем с Id:{}", requestId, userId);
        return itemRequestService.findRequestById(userId, requestId);
    }
}