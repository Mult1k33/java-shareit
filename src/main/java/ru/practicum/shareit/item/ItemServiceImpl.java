package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.comment.dao.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentNewDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemValidate;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    // Получение всех вещей пользователя
    @Override
    public Collection<ItemFullDto> findAllByUserId(Long userId) {
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(item -> {
                    BookingDto lastBooking = findLastBooking(item.getId(), userId);
                    BookingDto nextBooking = findNextBooking(item.getId(), userId);
                    List<CommentDto> comments = findCommentsForItem(item.getId());
                    return ItemMapper.mapToFullDto(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    // Получение вещи по id
    @Override
    public ItemFullDto findById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        BookingDto lastBooking = findLastBooking(itemId, userId);
        BookingDto nextBooking = findNextBooking(itemId, userId);
        List<CommentDto> comments = findCommentsForItem(itemId);

        return ItemMapper.mapToFullDto(item, lastBooking, nextBooking, comments);
    }

    // Поиск вещи по тексту в названии или описании
    @Override
    public Collection<ItemDto> searchByText(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByText(searchText.toLowerCase())
                .stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    // Добавление вещи
    @Override
    public ItemDto create(Long ownerId, ItemNewDto itemNewDto) {
        ItemValidate.validateForCreate(itemNewDto);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID" + ownerId + " не найден"));
        Item item = ItemMapper.mapToItem(itemNewDto, owner);
        Item createdItem = itemRepository.save(item);
        return ItemMapper.mapToDto(createdItem);
    }

    // Обновление вещи
    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemNewDto itemNewDto) {
        ItemValidate.validateForUpdate(itemNewDto);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + "не найдена"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            log.warn("Попытка изменения чужой вещи");
            throw new ForbiddenException("Пользователь не является владельцем вещи");
        }

        if (itemNewDto.getName() != null) {
            existingItem.setName(itemNewDto.getName());
        }
        if (itemNewDto.getDescription() != null) {
            existingItem.setDescription(itemNewDto.getDescription());
        }
        if (itemNewDto.getAvailable() != null) {
            existingItem.setAvailable(itemNewDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.mapToDto(updatedItem);
    }

    // Удаление вещи
    @Override
    public void delete(Long ownerId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + "не найдена"));

        if (!item.getOwner().getId().equals(ownerId)) {
            log.warn("Попытка удаления чужой вещи. Пользователь ID: {}, Владелец вещи ID: {}",
                    ownerId, item.getOwner().getId());
            throw new ForbiddenException("Недостаточно прав для удаления вещи");
        }

        itemRepository.deleteById(itemId);
    }

    // Добавление комментариев вещи
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentNewDto commentNewDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + "не найдена"));

        Collection<Booking> bookings = bookingRepository.findByBookerIdLastBookingsForItem(userId, itemId);
        if (bookings.isEmpty()) {
            log.error("Пользователь с id:{} не бронировал вещь с id:{}", userId, itemId);
            throw new ValidationException("Нельзя оставить комментарий, если пользователь не бронировал вещь");
        }

        Comment comment = Comment.builder()
                .text(commentNewDto.getText())
                .item(item)
                .author(author)
                .created(Instant.now())
                .build();
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.mapToDto(savedComment);
    }

    // Получение всех комментариев вещи
    @Override
    public Collection<CommentDto> findAllCommentsByItemId(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Вещь с ID " + itemId + " не найдена");
        }

        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    // Вспомогательный метод для поиска последнего бронирования для вещи
    private BookingDto findLastBooking(Long itemId, Long userId) {
        Collection<Booking> lastBookings = bookingRepository.findByOwnerIdLastBookingsForItem(itemId, userId);
        return lastBookings.stream()
                .findFirst()
                .map(BookingMapper::mapToDto)
                .orElse(null);
    }

    // Вспомогательный метод для поиска следующего бронирования для вещи
    private BookingDto findNextBooking(Long itemId, Long userId) {
        Collection<Booking> nextBookings = bookingRepository.findByOwnerIdNextBookingsForItem(itemId, userId);
        return nextBookings.stream()
                .findFirst()
                .map(BookingMapper::mapToDto)
                .orElse(null);
    }

    // Вспомогательный метод для получения комментариев для вещи
    private List<CommentDto> findCommentsForItem(Long itemId) {
        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToDto)
                .collect(Collectors.toList());
    }
}