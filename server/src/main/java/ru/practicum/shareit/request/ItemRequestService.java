package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.*;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestNewDto itemRequestNewDto);

    Collection<ItemRequestDto> findAllByUserId(Long userId);

    Collection<ItemRequestDto> findAllOtherUsersRequests(Long userId);

    ItemRequestDto findRequestById(Long userId, Long requestId);
}