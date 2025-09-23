package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServerIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();

        // Создание и сохранение тестового пользователя
        testUser = userRepository.save(User.builder()
                .name("Тестовый пользователь")
                .email("user@yandex.ru")
                .build());
    }

    // Тест получения всех пользователей из базы данных
    @Test
    public void findAll_shouldReturnAllUsers() {
        // создаем еще одного пользователя
        User secondUser = userRepository.save(User.builder()
                .name("Второй пользователь")
                .email("second@yandex.ru")
                .build());

        Collection<UserDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // Тест граничного случая получения всех пользователей(возврат пустого списка когда нет пользователей)
    @Test
    public void findAll_shouldReturnEmptyListWhenNoUsers() {
        userRepository.deleteAll();

        Collection<UserDto> result = userService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Тест получения пользователя по id
    @Test
    public void findById_shouldReturnUserById() {
        UserDto result = userService.findById(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals("Тестовый пользователь", result.getName());
        assertEquals("user@yandex.ru", result.getEmail());
    }

    // Тест граничного случая получения несуществующего пользователя по id
    @Test
    public void findById_shouldThrowNotFoundExceptionForNonExistentUser() {
        assertThrows(NotFoundException.class, () -> {
            userService.findById(999L);
        });
    }

    // Тест создания нового пользователя
    @Test
    public void create_shouldCreateNewUser() {
        UserNewDto newUserDto = new UserNewDto("Новый User", "new@email.com");

        UserDto result = userService.create(newUserDto);

        assertNotNull(result.getId());
        assertEquals("Новый User", result.getName());
        assertEquals("new@email.com", result.getEmail());

        // Проверяем, что пользователь сохранился в БД
        assertEquals(2, userRepository.count());
    }

    // Тест граничного случая создания нового пользователя для повторяющегося email(DuplicateException)
    @Test
    public void create_shouldThrowDuplicateExceptionForExistingEmail() {
        UserNewDto newUserDto = new UserNewDto("Пользователь 3", "user@yandex.ru"); // Существующий email

        assertThrows(DuplicateException.class, () -> {
            userService.create(newUserDto);
        });
    }

    // Тест граничного случая создания нового пользователя для email в другом регистре(DuplicateException)
    @Test
    public void create_shouldThrowDuplicateExceptionForCaseInsensitiveEmail() {
        UserNewDto newUserDto = new UserNewDto("Пользователь 4", "USER@yandex.ru"); // Email в верхнем регистре

        assertThrows(DuplicateException.class, () -> {
            userService.create(newUserDto);
        });
    }

    // Тест обновления пользователя
    @Test
    public void update_shouldUpdateUser() {
        UserNewDto updateDto = new UserNewDto("Пользователь для обновления", "updated@email.com");

        UserDto result = userService.update(testUser.getId(), updateDto);

        assertEquals("Пользователь для обновления", result.getName());
        assertEquals("updated@email.com", result.getEmail());

        // Проверяем, что изменения сохранились в БД
        User dbUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals("Пользователь для обновления", dbUser.getName());
        assertEquals("updated@email.com", dbUser.getEmail());
    }

    // Тест граничного случая частичного обновления пользователя(только имя)
    @Test
    public void update_shouldPartiallyUpdateUserName() {
        UserNewDto updateDto = new UserNewDto("Имя для обновления", null);

        UserDto result = userService.update(testUser.getId(), updateDto);

        assertEquals("Имя для обновления", result.getName());
        assertEquals("user@yandex.ru", result.getEmail()); // Email остался прежним
    }

    // Тест граничного случая частичного обновления пользователя(только email)
    @Test
    public void update_shouldPartiallyUpdateUserEmail() {
        UserNewDto updateDto = new UserNewDto(null, "updated@email.com");

        UserDto result = userService.update(testUser.getId(), updateDto);

        assertEquals("Тестовый пользователь", result.getName()); // Имя осталось прежним
        assertEquals("updated@email.com", result.getEmail());
    }

    // Тест граничного случая при обновлении на существующий email(DuplicateException)
    @Test
    public void update_shouldThrowDuplicateExceptionForExistingEmail() {
        // создаем второго пользователя
        User secondUser = userRepository.save(User.builder()
                .name("Second User")
                .email("second@email.com")
                .build());

        // Пытаемся обновить testUser на email второго пользователя
        UserNewDto updateDto = new UserNewDto("Updated User", "second@email.com");

        assertThrows(DuplicateException.class, () -> {
            userService.update(testUser.getId(), updateDto);
        });
    }

    // Тест граничного случая обновления несуществующего пользователя(NotFoundException)
    @Test
    public void update_shouldThrowNotFoundExceptionForNonExistentUser() {
        UserNewDto updateDto = new UserNewDto("Updated User", "updated@email.com");

        assertThrows(NotFoundException.class, () -> {
            userService.update(999L, updateDto);
        });
    }

    // Тест удаления пользователя
    @Test
    public void delete_shouldRemoveUserFromDatabase() {
        userService.delete(testUser.getId());

        assertFalse(userRepository.existsById(testUser.getId()));
        assertEquals(0, userRepository.count());
    }

    // Тест граничного случая удаления несуществующего пользователя(NotFoundException)
    @Test
    public void delete_shouldThrowNotFoundExceptionForNonExistentUser() {
        assertThrows(NotFoundException.class, () -> {
            userService.delete(999L);
        });
    }
}