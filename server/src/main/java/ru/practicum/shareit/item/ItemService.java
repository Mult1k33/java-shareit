package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.*;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<ItemFullDto> findAllByUserId(Long userId);

    ItemFullDto findById(Long userId, Long itemId);

    Collection<ItemDto> searchByText(String searchText);

    ItemDto create(Long ownerId, ItemNewDto newItem);

    ItemDto update(Long ownerId, Long itemId, ItemNewDto updatedItem);

    void delete(Long ownerID, Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentNewDto comment);

    Collection<CommentDto> findAllCommentsByItemId(Long itemId);

    Collection<ItemDto> findItemsByRequestIds(List<Long> requestIds);
}