package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new HashMap<>();
    private long idCounter = 0L;

    @Override
    public Collection<Item> findAllByUserId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> searchByText(String searchText) {
        String text = searchText.toLowerCase();
        return items.values()
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(text))
                        || (item.getDescription().toLowerCase().contains(text))
                        && (item.getAvailable().equals(true)))
                .toList();
    }

    @Override
    public Item create(Item item, Long userId) {
        item.setItemId(++idCounter);
        item.setOwnerId(userId);
        items.put(item.getItemId(), item);
        final List<Item> itemsByOwner = userItemIndex.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>());
        itemsByOwner.add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getItemId(), item);
        return item;
    }

    @Override
    public void delete(Long itemId) {
        items.remove(itemId);
    }
}