package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemValidate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    // Получение всех вещей пользователя
    @Override
    public Collection<ItemDto> findAllByUserId(Long userId) {
        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    // Получение вещи по id
    @Override
    public ItemDto findById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    // Поиск вещи по тексту в названии или описании
    @Override
    public Collection<ItemDto> searchByText(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByText(searchText.toLowerCase())
                .stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    // Добавление вещи
    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        UserDto user = userService.findById(ownerId);

        if (user == null) {
            throw new NotFoundException("Пользователь с ID" + ownerId + " не найден");
        }

        Item item = ItemMapper.mapToItem(itemDto);
        item.setOwnerId(ownerId);
        ItemValidate.validateForCreate(item);

        Item createdItem = itemRepository.create(item, ownerId);
        return ItemMapper.mapToDto(createdItem);
    }

    // Обновление вещи
    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        userService.findById(ownerId);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + "не найдена"));

        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Пользователь не является владельцем вещи");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        ItemValidate.validateForUpdate(existingItem);
        Item updatedItem = itemRepository.update(existingItem);
        return ItemMapper.mapToDto(updatedItem);
    }

    // Удаление вещи
    @Override
    public void delete(Long ownerId, Long itemId) {
        itemRepository.delete(itemId);
    }
}