package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemNewDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemFullDto> findAllByUserId(Long userId);

    ItemFullDto findById(Long userId, Long itemId);

    Collection<ItemDto> searchByText(String searchText);

    ItemDto create(Long ownerId, ItemNewDto newItem);

    ItemDto update(Long ownerId, Long itemId, ItemNewDto updatedItem);

    void delete(Long ownerID, Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentNewDto comment);

    Collection<CommentDto> findAllCommentsByItemId(Long itemId);
}