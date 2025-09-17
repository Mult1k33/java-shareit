package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User otherUser;
    private ItemRequest testRequest;

    @BeforeEach
    public void beforeEach() {
        // Очистка репозиториев
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        // Создание и сохранение тестового пользователя
        testUser = userRepository.save(User.builder()
                .name("Тестовый пользователь")
                .email("user@yandex.ru")
                .build());

        // Создание другого пользователя
        otherUser = userRepository.save(User.builder()
                .name("Новый пользователь")
                .email("other@yahoo.com")
                .build());

        // Создание тестового запроса
        testRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Нужна тестовая вещь")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build());
    }

    // Тест создания запроса на вещь
    @Test
    public void create_shouldCreateNewItemRequest() {
        ItemRequestNewDto newRequestDto = new ItemRequestNewDto("Нуждаюсь в вещи");

        ItemRequestDto result = itemRequestService.create(testUser.getId(), newRequestDto);

        assertNotNull(result.getId());
        assertEquals("Нуждаюсь в вещи", result.getDescription());
        assertNotNull(result.getCreated());
        assertTrue(result.getItems().isEmpty()); // Пока нет предложенных вещей
    }

    // Тест граничного случая создания запроса на вещь для несуществующего пользователя(NotFoundException)
    @Test
    public void create_shouldThrowNotFoundExceptionForNonExistentUser() {
        ItemRequestNewDto newRequestDto = new ItemRequestNewDto("Нужна тестовая вещь");

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.create(999L, newRequestDto);
        });
    }

    // Тест получения всех запросов текущего пользователя
    @Test
    public void findAllByUserId_shouldReturnUserRequests() {
        Collection<ItemRequestDto> result = itemRequestService.findAllByUserId(testUser.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Нужна тестовая вещь", result.iterator().next().getDescription());
    }

    // Тест граничного случая получения всех запросов (возврат пустого списка для пользователя без запросов)
    @Test
    public void findAllByUserId_shouldReturnEmptyListForUserWithoutRequests() {
        Collection<ItemRequestDto> result = itemRequestService.findAllByUserId(otherUser.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Тест получения запросов от других пользователей
    @Test
    public void findAllOtherUsersRequests_shouldReturnOtherUsersRequests() {
        // otherUser запрашивает запросы других пользователей (testUser)
        Collection<ItemRequestDto> result = itemRequestService.findAllOtherUsersRequests(otherUser.getId());

        assertNotNull(result);
        assertEquals(1, result.size()); // Запрос testUser должен быть виден
        assertEquals("Нужна тестовая вещь", result.iterator().next().getDescription());
    }

    // Тест граничного случая получения запросов (пользователь не видит свои собственные запросы)
    @Test
    public void findAllOtherUsersRequests_shouldNotReturnOwnRequests() {
        Collection<ItemRequestDto> result = itemRequestService.findAllOtherUsersRequests(testUser.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Тест получения запроса по id
    @Test
    public void findRequestById_shouldReturnRequestById() {
        ItemRequestDto result = itemRequestService.findRequestById(testUser.getId(), testRequest.getId());

        assertNotNull(result);
        assertEquals(testRequest.getId(), result.getId());
        assertEquals("Нужна тестовая вещь", result.getDescription());
        assertTrue(result.getItems().isEmpty()); // Пока нет предложенных вещей
    }

    // Тест граничного случая получения несуществующего запроса по id(NotFoundException)
    @Test
    public void findRequestById_shouldThrowNotFoundExceptionForNonExistentRequest() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.findRequestById(testUser.getId(), 999L);
        });
    }
}