package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.comment.dto.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemServerController.class)
@Import(ErrorHandler.class)
public class ItemServerControllerTest {

    private final String url = "/items";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final Long VALID_USER_ID = 1L;
    private static final Long VALID_ITEM_ID = 1L;
    private static final Long VALID_COMMENT_ID = 1L;
    private static final Long NON_EXISTENT_ID = 999L;
    private static final String SEARCH_TEXT = "test";
    private static final String EMPTY_SEARCH_TEXT = "";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemFullDto itemFullDto;
    private ItemNewDto itemNewDto;
    private CommentDto commentDto;
    private CommentNewDto commentNewDto;

    @BeforeEach
    public void setUp() {
        Mockito.reset(itemService);

        UserDto userDto = UserDto.builder()
                .id(VALID_USER_ID)
                .name("Тестовый пользователь")
                .email("test@yandex.ru")
                .build();

        itemDto = ItemDto.builder()
                .id(VALID_ITEM_ID)
                .name("Тестовая вещь")
                .description("Описание")
                .available(true)
                .build();

        itemFullDto = ItemFullDto.builder()
                .id(VALID_ITEM_ID)
                .name("Тестовая вещь")
                .description("Описание вещи")
                .available(true)
                .ownerId(VALID_USER_ID)
                .build();

        itemNewDto = new ItemNewDto("Тестовая вещь", "Описание", true, null);

        commentDto = CommentDto.builder()
                .id(VALID_COMMENT_ID)
                .text("Текст отзыва")
                .authorName("Тестовый пользователь")
                .created(Instant.now())
                .build();

        commentNewDto = new CommentNewDto("Текст отзыва");
        setupDefaultSuccessfulMocks();
    }

    // Тест успешного получения всех вещей пользователя
    @Test
    public void findAllByUserId_shouldReturnUserItems() throws Exception {
        mvc.perform(get(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemService).findAllByUserId(VALID_USER_ID);
    }

    // Тест получения пустого списка вещей пользователя
    @Test
    public void findAllByUserId_shouldReturnEmptyList_whenNoItems() throws Exception {
        when(itemService.findAllByUserId(VALID_USER_ID))
                .thenReturn(List.of());

        mvc.perform(get(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(itemService).findAllByUserId(VALID_USER_ID);
    }

    // Тест успешного получения вещи по id
    @Test
    public void findById_shouldReturnItem() throws Exception {
        mvc.perform(get("/items/{itemId}", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemService).findById(VALID_USER_ID, VALID_ITEM_ID);
    }

    // Тест получения несуществующей вещи
    @Test
    public void findById_shouldReturnNotFound_whenItemNotExists() throws Exception {
        when(itemService.findById(VALID_USER_ID, NON_EXISTENT_ID))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mvc.perform(get("/items/{itemId}", NON_EXISTENT_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isNotFound());

        verify(itemService).findById(VALID_USER_ID, NON_EXISTENT_ID);
    }

    // Тест успешного поиска вещей по тексту
    @Test
    public void searchByText_shouldReturnFoundItems() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", SEARCH_TEXT))
                .andExpect(status().isOk());

        verify(itemService).searchByText(SEARCH_TEXT);
    }

    // Тест поиска с пустым текстом
    @Test
    public void searchByText_shouldReturnEmptyList_whenTextIsEmpty() throws Exception {
        when(itemService.searchByText(EMPTY_SEARCH_TEXT))
                .thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", EMPTY_SEARCH_TEXT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(itemService).searchByText(EMPTY_SEARCH_TEXT);
    }

    // Тест успешного создания новой вещи
    @Test
    public void create_shouldCreateItem() throws Exception {
        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemNewDto)))
                .andExpect(status().isCreated());

        verify(itemService).create(eq(VALID_USER_ID), any(ItemNewDto.class));
    }

    // Тест создания вещи для несуществующего пользователя
    @Test
    public void create_shouldReturnNotFound_whenUserNotExists() throws Exception {
        when(itemService.create(eq(NON_EXISTENT_ID), any(ItemNewDto.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemNewDto)))
                .andExpect(status().isNotFound());

        verify(itemService).create(eq(NON_EXISTENT_ID), any(ItemNewDto.class));
    }

    // Тест успешного обновления вещи
    @Test
    public void update_shouldUpdateItem() throws Exception {
        mvc.perform(patch("/items/{itemId}", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemNewDto)))
                .andExpect(status().isOk());

        verify(itemService).update(eq(VALID_USER_ID), eq(VALID_ITEM_ID), any(ItemNewDto.class));
    }

    // Тест обновления несуществующей вещи
    @Test
    public void update_shouldReturnNotFound_whenItemNotExists() throws Exception {
        when(itemService.update(eq(VALID_USER_ID), eq(NON_EXISTENT_ID), any(ItemNewDto.class)))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mvc.perform(patch("/items/{itemId}", NON_EXISTENT_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemNewDto)))
                .andExpect(status().isNotFound());

        verify(itemService).update(eq(VALID_USER_ID), eq(NON_EXISTENT_ID), any(ItemNewDto.class));
    }

    // Тест успешного удаления вещи
    @Test
    public void delete_shouldDeleteItem() throws Exception {
        mvc.perform(delete("/items/{itemId}", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isNoContent());

        verify(itemService).delete(VALID_USER_ID, VALID_ITEM_ID);
    }

    // Тест удаления несуществующей вещи
    @Test
    public void delete_shouldReturnNotFound_whenItemNotExists() throws Exception {
        doThrow(new NotFoundException("Вещь не найдена"))
                .when(itemService).delete(VALID_USER_ID, NON_EXISTENT_ID);

        mvc.perform(delete("/items/{itemId}", NON_EXISTENT_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isNotFound());

        verify(itemService).delete(VALID_USER_ID, NON_EXISTENT_ID);
    }

    // Тест успешного добавления комментария
    @Test
    public void addComment_shouldAddComment() throws Exception {
        mvc.perform(post("/items/{itemId}/comment", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentNewDto)))
                .andExpect(status().isCreated());

        verify(itemService).addComment(eq(VALID_USER_ID), eq(VALID_ITEM_ID), any(CommentNewDto.class));
    }

    // Тест добавления комментария к несуществующей вещи
    @Test
    public void addComment_shouldReturnNotFound_whenItemNotExists() throws Exception {
        when(itemService.addComment(eq(VALID_USER_ID), eq(NON_EXISTENT_ID), any(CommentNewDto.class)))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mvc.perform(post("/items/{itemId}/comment", NON_EXISTENT_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentNewDto)))
                .andExpect(status().isNotFound());

        verify(itemService).addComment(eq(VALID_USER_ID), eq(NON_EXISTENT_ID), any(CommentNewDto.class));
    }

    // Тест успешного получения всех комментариев вещи
    @Test
    public void findAllCommentsByItemId_shouldReturnComments() throws Exception {
        mvc.perform(get("/items/{itemId}/comment", VALID_ITEM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(itemService).findAllCommentsByItemId(VALID_ITEM_ID);
    }

    // Тест получения комментариев несуществующей вещи
    @Test
    public void findAllCommentsByItemId_shouldReturnNotFound_whenItemNotExists() throws Exception {
        when(itemService.findAllCommentsByItemId(NON_EXISTENT_ID))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mvc.perform(get("/items/{itemId}/comment", NON_EXISTENT_ID))
                .andExpect(status().isNotFound());

        verify(itemService).findAllCommentsByItemId(NON_EXISTENT_ID);
    }

    // Вспомогательный метод для настройки мок-объектов
    private void setupDefaultSuccessfulMocks() {
        when(itemService.findAllByUserId(VALID_USER_ID))
                .thenReturn(List.of(itemFullDto));
        when(itemService.findById(VALID_USER_ID, VALID_ITEM_ID))
                .thenReturn(itemFullDto);
        when(itemService.searchByText(SEARCH_TEXT))
                .thenReturn(List.of(itemDto));
        when(itemService.create(eq(VALID_USER_ID), any(ItemNewDto.class)))
                .thenReturn(itemDto);
        when(itemService.update(eq(VALID_USER_ID), eq(VALID_ITEM_ID), any(ItemNewDto.class)))
                .thenReturn(itemDto);
        when(itemService.addComment(eq(VALID_USER_ID), eq(VALID_ITEM_ID), any(CommentNewDto.class)))
                .thenReturn(commentDto);
        when(itemService.findAllCommentsByItemId(VALID_ITEM_ID))
                .thenReturn(List.of(commentDto));
    }
}