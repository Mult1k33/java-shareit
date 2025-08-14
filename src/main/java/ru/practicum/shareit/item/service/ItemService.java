package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> findAllByUserId(Long userId);

    ItemDto findById(Long itemId);

    Collection<ItemDto> searchByText(String searchText);

    ItemDto create(Long ownerId, ItemDto newItem);

    ItemDto update(Long ownerId, Long itemId, ItemDto updatedItem);

    void delete(Long ownerID, Long itemId);
}