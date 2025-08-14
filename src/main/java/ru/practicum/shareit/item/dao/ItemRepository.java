package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Collection<Item> findAllByUserId(Long userId);

    Optional<Item> findById(Long itemId);

    Collection<Item> searchByText(String searchText);

    Item create(Item item, Long userId);

    Item update(Item item);

    void delete(Long itemId);
}