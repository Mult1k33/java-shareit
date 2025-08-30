package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.comment.dto.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User testUser;
    private User testBooker;
    private User otherUser;
    private Item testItem;
    private Item unavailableItem;

    @BeforeEach
    public void beforeEach() {
        // Очистка базы перед каждым тестом
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRequestRepository.deleteAll();

        // Создание и сохранение тестового владельца
        testUser = userRepository.save(User.builder()
                .name("Тестовый владелец")
                .email("owner@yandex.ru")
                .build());

        // Создание и сохранение арендатора
        testBooker = userRepository.save(User.builder()
                .name("Тестовый арендатор")
                .email("booker@gmail.com")
                .build());

        // Создание другого пользователя
        otherUser = userRepository.save(User.builder()
                .name("Новый пользователь")
                .email("other@yahoo.com")
                .build());

        // Создание и сохранение тестовой вещи
        testItem = itemRepository.save(Item.builder()
                .name("Тестовая вещь")
                .description("Описание")
                .available(true)
                .owner(testUser)
                .build());

        // Создание недоступной вещи
        unavailableItem = itemRepository.save(Item.builder()
                .name("Недоступная вещь")
                .description("Описание вещи")
                .available(false)
                .owner(testUser)
                .build());
    }

    // Тест получения всех вещей пользователя с полной информацией
    @Test
    public void findAllByUserId_shouldReturnUserItemsWithFullInfo() {
        Collection<ItemFullDto> result = itemService.findAllByUserId(testUser.getId());

        assertNotNull(result);
        assertEquals(2, result.size()); // 2 вещи: testItem и unavailableItem
    }

    // Тест граничного случая получения всех вещей пользователя(возврат пустого списка для пользователя без вещей)
    @Test
    public void findAllByUserId_shouldReturnEmptyListForUserWithoutItems() {
        Collection<ItemFullDto> result = itemService.findAllByUserId(otherUser.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Тест получения вещи по id
    @Test
    public void findById_shouldReturnItemWithFullInfo() {
        ItemFullDto result = itemService.findById(testUser.getId(), testItem.getId());

        assertNotNull(result);
        assertEquals("Тестовая вещь", result.getName());
        assertEquals("Описание", result.getDescription());
        assertTrue(result.getAvailable());
    }

    // Тест граничного случая получения несуществующей вещи по id(NotFoundException)
    @Test
    public void findById_shouldThrowNotFoundExceptionForNonExistentItem() {
        assertThrows(NotFoundException.class, () -> {
            itemService.findById(testUser.getId(), 999L);
        });
    }

    // Тест поиска вещи по тексту в названии или описании
    @Test
    public void searchByText_shouldFindItemsByText() {
        Collection<ItemDto> result = itemService.searchByText("тест");

        assertEquals(1, result.size());
        assertEquals("Тестовая вещь", result.iterator().next().getName());
    }

    // Тест граничного случая поиска вещи по тексту с пустым запросом
    @Test
    public void searchByText_shouldReturnEmptyListForEmptyText() {
        Collection<ItemDto> result = itemService.searchByText("");

        assertTrue(result.isEmpty());
    }

    // Тест граничного случая поиска вещи по тексту (поиск только доступных вещей)
    @Test
    public void searchByText_shouldReturnOnlyAvailableItems() {
        Collection<ItemDto> result = itemService.searchByText("недоступная");

        assertTrue(result.isEmpty()); // unavailableItem не доступен для бронирования
    }

    // Тест добавления новой вещи и сохранения в БД
    @Test
    public void create_shouldSaveNewItemToDatabase() {
        ItemNewDto newItemDto = new ItemNewDto("Новая вещь", "Новое описание",
                true, null);

        ItemDto createdItem = itemService.create(testUser.getId(), newItemDto);

        assertNotNull(createdItem.getId());
        assertEquals("Новая вещь", createdItem.getName());
        assertEquals("Новое описание", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());

        // Проверяем, что вещь сохранилась в БД
        assertEquals(3, itemRepository.count());
    }

    // Тест граничного случая добавления новой вещи(создание вещи с запросом)
    @Test
    public void create_shouldSaveItemWithRequest() {
        ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
                .description("Нужна тестовая вещь")
                .requester(testBooker)
                .created(LocalDateTime.now())
                .build());

        ItemNewDto newItemDto = new ItemNewDto("Вещь для запроса", "Описания вещи для запроса",
                true, request.getId());

        ItemDto createdItem = itemService.create(testUser.getId(), newItemDto);

        assertNotNull(createdItem.getId());
        assertEquals("Вещь для запроса", createdItem.getName());
    }

    // Тест граничного случая добавления новой вещи для несуществующего пользователя(NotFoundException)
    @Test
    public void create_shouldThrowNotFoundExceptionForNonExistentUser() {
        ItemNewDto newItemDto = new ItemNewDto("Тест вещь", "Описание тест вещи",
                true, null);

        assertThrows(NotFoundException.class, () -> {
            itemService.create(999L, newItemDto);
        });
    }

    // Тест обновления данных вещи в БД
    @Test
    public void update_shouldUpdateItemInDatabase() {
        ItemNewDto updateDto = new ItemNewDto("Вещь для обновления", "Описание для обновления",
                false, null);

        ItemDto updatedItem = itemService.update(testUser.getId(), testItem.getId(), updateDto);

        assertEquals("Вещь для обновления", updatedItem.getName());
        assertEquals("Описание для обновления", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());

        // Проверяем, что изменения сохранились в БД
        Item dbItem = itemRepository.findById(testItem.getId()).orElseThrow();
        assertEquals("Вещь для обновления", dbItem.getName());
        assertEquals("Описание для обновления", dbItem.getDescription());
        assertFalse(dbItem.getAvailable());
    }

    // Тест граничного случая обновления вещи(частичное обновление)
    @Test
    public void update_shouldPartiallyUpdateItem() {
        ItemNewDto updateDto = new ItemNewDto("Название", null, null, null);

        ItemDto updatedItem = itemService.update(testUser.getId(), testItem.getId(), updateDto);

        assertEquals("Название", updatedItem.getName());
        assertEquals("Описание", updatedItem.getDescription()); // Осталось прежним
        assertTrue(updatedItem.getAvailable()); // Осталось прежним
    }

    // Тест граничного случая при попытке обновления чужой вещи(ForbiddenException)
    @Test
    public void update_shouldThrowForbiddenExceptionForOtherUser() {
        ItemNewDto updateDto = new ItemNewDto("Измененная вещь", "Измененное описание",
                false, null);

        assertThrows(ForbiddenException.class, () -> {
            itemService.update(otherUser.getId(), testItem.getId(), updateDto);
        });
    }

    // Тест удаления вещи из БД
    @Test
    public void delete_shouldRemoveItemFromDatabase() {
        itemService.delete(testUser.getId(), testItem.getId());

        assertFalse(itemRepository.existsById(testItem.getId()));
        assertEquals(1, itemRepository.count()); // Осталась только unavailableItem
    }

    // Тест граничного случая при попытке удалить чужую вещь(ForbiddenException)
    @Test
    public void delete_shouldThrowForbiddenExceptionForOtherUser() {
        assertThrows(ForbiddenException.class, () -> {
            itemService.delete(otherUser.getId(), testItem.getId());
        });
    }

    // Тест добавления отзыва к вещи после бронирования
    @Test
    public void addComment_shouldAddCommentAfterBooking() {
        // создаем бронирование
        Booking booking = bookingRepository.save(Booking.builder()
                .start(Instant.now().minus(2, ChronoUnit.DAYS))
                .end(Instant.now().minus(1, ChronoUnit.DAYS))
                .item(testItem)
                .booker(testBooker)
                .status(BookingStatus.APPROVED)
                .build());

        CommentNewDto commentDto = new CommentNewDto("Все супер!");

        CommentDto result = itemService.addComment(testBooker.getId(), testItem.getId(), commentDto);

        assertNotNull(result.getId());
        assertEquals("Все супер!", result.getText());
        assertEquals("Тестовый арендатор", result.getAuthorName());

        // Проверяем, что комментарий сохранился
        assertEquals(1, itemService.findAllCommentsByItemId(testItem.getId()).size());
    }

    // Тест граничного случая при попытке добавить отзыв при отсутствии бронирования
    @Test
    public void addComment_shouldThrowValidationExceptionWithoutBooking() {
        CommentNewDto commentDto = new CommentNewDto("Рекомендую!");

        assertThrows(ValidationException.class, () -> {
            itemService.addComment(testBooker.getId(), testItem.getId(), commentDto);
        });
    }

    // Тест получения всех отзывов о вещи
    @Test
    public void findAllCommentsByItemId_shouldReturnItemComments() {
        Collection<CommentDto> result = itemService.findAllCommentsByItemId(testItem.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty()); // Пока нет комментариев
    }

    // Тест граничного случая получения всех отзывов у несуществующей вещи(NotFoundException)
    @Test
    public void findAllCommentsByItemId_shouldThrowNotFoundExceptionForNonExistentItem() {
        // when & then
        assertThrows(NotFoundException.class, () -> {
            itemService.findAllCommentsByItemId(999L);
        });
    }

    // Тест получения вещи по Id запроса
    @Test
    public void findItemsByRequestIds_shouldReturnItemsForRequests() {
        ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
                .description("Необходима вещь")
                .requester(testBooker)
                .created(LocalDateTime.now())
                .build());

        Item requestedItem = itemRepository.save(Item.builder()
                .name("Вещь для запроса")
                .description("Для теста")
                .available(true)
                .owner(testUser)
                .request(request)
                .build());

        Collection<ItemDto> result = itemService.findItemsByRequestIds(List.of(request.getId()));

        assertEquals(1, result.size());
        assertEquals("Вещь для запроса", result.iterator().next().getName());
    }

    // Тест граничного случая получения вещи по Id запроса(возврат пустого списка для несуществующего запроса)
    @Test
    public void findItemsByRequestIds_shouldReturnEmptyListForNonExistentRequests() {
        Collection<ItemDto> result = itemService.findItemsByRequestIds(List.of(999L));

        assertTrue(result.isEmpty());
    }

    // Тест граничного получения вещи по Id запроса(возврат пустого списка для пустого списка запросов)
    @Test
    public void findItemsByRequestIds_shouldReturnEmptyListForEmptyRequestList() {
        Collection<ItemDto> result = itemService.findItemsByRequestIds(List.of());

        assertTrue(result.isEmpty());
    }
}