package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.utils.ItemRequestValidate;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // Создание нового запроса
    @Override
    public ItemRequestDto create(Long userId, ItemRequestNewDto itemRequestNewDto) {
        ItemRequestValidate.validateForCreate(itemRequestNewDto);
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID" + userId + " не найден"));

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestNewDto, requester);
        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToDto(createdItemRequest);
    }

    // Получение всех запросов текущего пользователя
    @Override
    public Collection<ItemRequestDto> findAllByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return enrichRequestsWithItems(requests);
    }

    // Получение всех запросов от других пользователей
    @Override
    public Collection<ItemRequestDto> findAllOtherUsersRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        return enrichRequestsWithItems(requests);
    }

    // Получение запроса по id
    @Override
    public ItemRequestDto findRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + requestId + " не найден"));

        Collection<Item> items = itemRepository.findByRequestId(requestId);
        Collection<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.mapToDtoWithItems(request, itemDtos);
    }

    // Вспомогательный метод для получения запросов для вещи
    private Collection<ItemRequestDto> enrichRequestsWithItems(Collection<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        // Собираем все ID запросов
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        // Загружаем все items для всех запросов одним запросом
        Collection<Item> allItems = itemRepository.findByRequestIds(requestIds);

        // Группируем items по requestId
        Map<Long, List<ItemDto>> itemsByRequestId = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::mapToDto, Collectors.toList())
                ));

        // Обогащаем каждый запрос его items
        return requests.stream()
                .map(request -> {
                    Collection<ItemDto> itemsForRequest = itemsByRequestId.getOrDefault(
                            request.getId(), Collections.emptyList());
                    return ItemRequestMapper.mapToDtoWithItems(request, itemsForRequest);
                })
                .collect(Collectors.toList());
    }
}