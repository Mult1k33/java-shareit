package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.dto.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserServerController.class)
@Import(ErrorHandler.class)
public class UserServerControllerTest {

    private final String url = "/users";
    private static final Long VALID_USER_ID = 1L;
    private static final Long NON_EXISTENT_ID = 999L;
    private static final String USER_NAME = "Тестовый пользователь";
    private static final String USER_EMAIL = "test@yandex.ru";
    private static final String UPDATED_NAME = "Обновленное имя";
    private static final String UPDATED_EMAIL = "updated@yandex.ru";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto userDto;
    private UserNewDto userNewDto;
    private UserNewDto userUpdateDto;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userService);

        userDto = UserDto.builder()
                .id(VALID_USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        userNewDto = new UserNewDto(USER_NAME, USER_EMAIL);
        userUpdateDto = new UserNewDto(UPDATED_NAME, UPDATED_EMAIL);

        setupDefaultSuccessfulMocks();
    }

    //Тест успешного получения всех пользователей
    @Test
    public void findAll_shouldReturnAllUsers() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService).findAll();
    }

    // Тест получения пустого списка пользователей
    @Test
    public void findAll_shouldReturnEmptyList_whenNoUsers() throws Exception {
        when(userService.findAll())
                .thenReturn(List.of());

        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService).findAll();
    }

    //Тест успешного получения пользователя по ID
    @Test
    public void findById_shouldReturnUser() throws Exception {
        mvc.perform(get("/users/{userId}", VALID_USER_ID))
                .andExpect(status().isOk());

        verify(userService).findById(VALID_USER_ID);
    }

    //Тест получения несуществующего пользователя
    @Test
    public void findById_shouldReturnNotFound_whenUserNotExists() throws Exception {
        when(userService.findById(NON_EXISTENT_ID))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(get("/users/{userId}", NON_EXISTENT_ID))
                .andExpect(status().isNotFound());

        verify(userService).findById(NON_EXISTENT_ID);
    }

    //Тест успешного создания нового пользователя
    @Test
    public void create_shouldCreateUser() throws Exception {
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userNewDto)))
                .andExpect(status().isCreated());

        verify(userService).create(any(UserNewDto.class));
    }

    // Тест успешного обновления пользователя
    @Test
    public void update_shouldUpdateUser() throws Exception {
        UserDto updatedUserDto = UserDto.builder()
                .id(VALID_USER_ID)
                .name(UPDATED_NAME)
                .email(UPDATED_EMAIL)
                .build();

        when(userService.update(eq(VALID_USER_ID), any(UserNewDto.class)))
                .thenReturn(updatedUserDto);

        mvc.perform(patch("/users/{userId}", VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(UPDATED_NAME))
                .andExpect(jsonPath("$.email").value(UPDATED_EMAIL));

        verify(userService).update(eq(VALID_USER_ID), any(UserNewDto.class));
    }

    //Тест обновления несуществующего пользователя
    @Test
    public void update_shouldReturnNotFound_whenUserNotExists() throws Exception {
        when(userService.update(eq(NON_EXISTENT_ID), any(UserNewDto.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(patch("/users/{userId}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isNotFound());

        verify(userService).update(eq(NON_EXISTENT_ID), any(UserNewDto.class));
    }

    // Тест успешного удаления пользователя
    @Test
    public void delete_shouldDeleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", VALID_USER_ID))
                .andExpect(status().isNoContent());

        verify(userService).delete(VALID_USER_ID);
    }

    // Тест удаления несуществующего пользователя
    @Test
    public void delete_shouldReturnNotFound_whenUserNotExists() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(userService).delete(NON_EXISTENT_ID);

        mvc.perform(delete("/users/{userId}", NON_EXISTENT_ID))
                .andExpect(status().isNotFound());

        verify(userService).delete(NON_EXISTENT_ID);
    }

    // Вспомогательный метод для настройки мок-объектов
    private void setupDefaultSuccessfulMocks() {
        when(userService.findAll())
                .thenReturn(List.of(userDto));
        when(userService.findById(VALID_USER_ID))
                .thenReturn(userDto);
        when(userService.create(any(UserNewDto.class)))
                .thenReturn(userDto);
        when(userService.update(eq(VALID_USER_ID), any(UserNewDto.class)))
                .thenReturn(userDto);
    }
}