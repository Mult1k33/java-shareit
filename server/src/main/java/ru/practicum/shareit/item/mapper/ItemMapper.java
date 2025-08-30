package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {

    // Преобразование Item в Dto
    public static ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    // Преобразование ItemNewDto в Item
    public static Item mapToItem(ItemNewDto itemNewDto, User owner, ItemRequest request) {
        return Item.builder()
                .name(itemNewDto.getName())
                .description(itemNewDto.getDescription())
                .available(itemNewDto.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }

    // Преобразование Item в ItemFullDto
    public static ItemFullDto mapToFullDto(Item item, BookingDto lastBooking,
                                           BookingDto nextBooking, List<CommentDto> comments) {
        return ItemFullDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}