package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestServerController.class)
@Import(ErrorHandler.class)
public class ItemRequestServerControllerTest {

    private final String url = "/requests";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final Long VALID_USER_ID = 1L;
    private static final Long VALID_REQUEST_ID = 1L;
    private static final Long NON_EXISTENT_ID = 999L;
    private static final String REQUEST_DESCRIPTION = "Нужна тестовая вещь";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;
    private ItemRequestNewDto itemRequestNewDto;

    @BeforeEach
    public void setUp() {
        Mockito.reset(itemRequestService);

        UserDto userDto = UserDto.builder()
                .id(VALID_USER_ID)
                .name("Тестовый пользователь")
                .email("test@yandex.ru")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(VALID_REQUEST_ID)
                .description(REQUEST_DESCRIPTION)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        itemRequestNewDto = new ItemRequestNewDto(REQUEST_DESCRIPTION);

        setupDefaultSuccessfulMocks();
    }

    // Тест успешного создания нового запроса на вещь
    @Test
    public void create_shouldCreateItemRequest() throws Exception {
        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestNewDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(REQUEST_DESCRIPTION));

        verify(itemRequestService).create(eq(VALID_USER_ID), any(ItemRequestNewDto.class));
    }

    // Тест создания запроса для несуществующего пользователя
    @Test
    public void create_shouldReturnNotFound_whenUserNotExists() throws Exception {
        when(itemRequestService.create(eq(NON_EXISTENT_ID), any(ItemRequestNewDto.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestNewDto)))
                .andExpect(status().isNotFound());

        verify(itemRequestService).create(eq(NON_EXISTENT_ID), any(ItemRequestNewDto.class));
    }

    // Тест успешного получения всех запросов пользователя
    @Test
    public void findAllByUserId_shouldReturnUserRequests() throws Exception {
        mvc.perform(get(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(itemRequestService).findAllByUserId(VALID_USER_ID);
    }

    // Тест получения пустого списка запросов пользователя
    @Test
    public void findAllByUserId_shouldReturnEmptyList_whenNoRequests() throws Exception {
        when(itemRequestService.findAllByUserId(VALID_USER_ID))
                .thenReturn(List.of());

        mvc.perform(get(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(itemRequestService).findAllByUserId(VALID_USER_ID);
    }

    // Тест успешного получения всех запросов других пользователей
    @Test
    public void findAllOtherUsersRequests_shouldReturnOtherUsersRequests() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestService).findAllOtherUsersRequests(VALID_USER_ID);
    }

    // Тест получения пустого списка запросов других пользователей
    @Test
    public void findAllOtherUsersRequests_shouldReturnEmptyList_whenNoOtherRequests() throws Exception {
        when(itemRequestService.findAllOtherUsersRequests(VALID_USER_ID))
                .thenReturn(List.of());

        mvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestService).findAllOtherUsersRequests(VALID_USER_ID);
    }

    // Тест успешного получения запроса по ID
    @Test
    public void findRequestById_shouldReturnRequest() throws Exception {
        mvc.perform(get("/requests/{requestId}", VALID_REQUEST_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestService).findRequestById(VALID_USER_ID, VALID_REQUEST_ID);
    }

    // Тест получения несуществующего запроса
    @Test
    public void findRequestById_shouldReturnNotFound_whenRequestNotExists() throws Exception {
        when(itemRequestService.findRequestById(VALID_USER_ID, NON_EXISTENT_ID))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mvc.perform(get("/requests/{requestId}", NON_EXISTENT_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isNotFound());

        verify(itemRequestService).findRequestById(VALID_USER_ID, NON_EXISTENT_ID);
    }

    // Тест получения запроса с неверными параметрами пути
    @Test
    public void findRequestById_shouldReturnBadRequest_whenInvalidPath() throws Exception {
        mvc.perform(get("/requests/invalid")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).findRequestById(anyLong(), anyLong());
    }

    // Вспомогательный метод для настройки мок-объектов
    private void setupDefaultSuccessfulMocks() {
        when(itemRequestService.create(eq(VALID_USER_ID), any(ItemRequestNewDto.class)))
                .thenReturn(itemRequestDto);
        when(itemRequestService.findAllByUserId(VALID_USER_ID))
                .thenReturn(List.of(itemRequestDto));
        when(itemRequestService.findAllOtherUsersRequests(VALID_USER_ID))
                .thenReturn(List.of(itemRequestDto));
        when(itemRequestService.findRequestById(VALID_USER_ID, VALID_REQUEST_ID))
                .thenReturn(itemRequestDto);
    }
}