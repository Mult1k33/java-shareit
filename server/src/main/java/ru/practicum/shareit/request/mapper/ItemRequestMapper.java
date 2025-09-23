package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

public class ItemRequestMapper {

    // Преобразование ItemRequestNewDto в ItemRequest
    public static ItemRequest mapToItemRequest(ItemRequestNewDto itemRequestNewDto, User requester) {
        return ItemRequest.builder()
                .description(itemRequestNewDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    // Преобразование ItemRequest в Dto (без items)
    public static ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();
    }

    // Преобразование ItemRequest в Dto с items (принимает items как параметр)
    public static ItemRequestDto mapToDtoWithItems(ItemRequest itemRequest, Collection<ItemDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items != null ? items : Collections.emptyList())
                .build();
    }
}