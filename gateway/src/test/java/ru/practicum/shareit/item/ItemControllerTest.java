package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.GatewayErrorHandler;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(GatewayErrorHandler.class)
public class ItemControllerTest {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final Long VALID_USER_ID = 1L;
    private static final Long VALID_ITEM_ID = 1L;
    private static final Long INVALID_ID = -1L;
    private static final Long VALID_REQUEST_ID = 1L;
    private final String url = "/items";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemClient);
        setupDefaultSuccessfulMocks();
    }

    @Test
    public void getAllItemsByOwner_WithValidParams_ReturnsOk() throws Exception {
        mvc.perform(get(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemClient).getAllItemsByOwner(eq(VALID_USER_ID));
    }

    @Test
    public void getAllItemsByOwner_WithNegativeUserId_ReturnsBadRequest() throws Exception {
        mvc.perform(get(url)
                        .header(X_SHARER_USER_ID, INVALID_ID))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllItemsByOwner(anyLong());
    }

    @Test
    public void getAllItemsByOwner_WithoutUserIdHeader_ReturnsBadRequest() throws Exception {
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllItemsByOwner(anyLong());
    }

    @Test
    public void getItemById_WithValidParams_ReturnsOk() throws Exception {
        mvc.perform(get("/items/{itemId}", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemClient).getItemById(eq(VALID_USER_ID), eq(VALID_ITEM_ID));
    }

    @Test
    public void getItemById_WithNegativeItemId_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/items/{itemId}", INVALID_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItemById(anyLong(), anyLong());
    }

    @Test
    public void getItemById_WithNegativeUserId_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/items/{itemId}", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, INVALID_ID))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItemById(anyLong(), anyLong());
    }

    @Test
    public void searchByText_WithValidParams_ReturnsOk() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "test search")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemClient).searchByText(eq(VALID_USER_ID), eq("test search"));
    }

    @Test
    public void searchByText_WithoutTextParam_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/items/search")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchByText(anyLong(), any());
    }

    @Test
    public void searchByText_WithEmptyText_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchByText(anyLong(), any());
    }

    @Test
    public void createItem_WithValidData_ReturnsCreated() throws Exception {
        ItemDtoRequest item = ItemDtoRequest.builder()
                .name("Тестовая вещь")
                .description("Описание вещи")
                .available(true)
                .requestId(VALID_REQUEST_ID)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(itemClient).createItem(eq(VALID_USER_ID), any(ItemDtoRequest.class));
    }

    @Test
    public void createItem_WithNullRequestId_ReturnsCreated() throws Exception {
        ItemDtoRequest item = ItemDtoRequest.builder()
                .name("Тестовая вещь")
                .description("Описание вещи")
                .available(true)
                .requestId(null)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(itemClient).createItem(eq(VALID_USER_ID), any(ItemDtoRequest.class));
    }

    @Test
    public void createItem_WithNegativeRequestId_ReturnsBadRequest() throws Exception {
        ItemDtoRequest item = ItemDtoRequest.builder()
                .name("Тестовая вещь")
                .description("Описание вещи")
                .available(true)
                .requestId(INVALID_ID)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(anyLong(), any());
    }

    @Test
    public void createItem_WithZeroRequestId_ReturnsBadRequest() throws Exception {
        ItemDtoRequest item = ItemDtoRequest.builder()
                .name("Тестовая вещь")
                .description("Описание вещи")
                .available(true)
                .requestId(0L)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(anyLong(), any());
    }

    @Test
    public void createItem_WithMultipleValidationErrors_ReturnsBadRequest() throws Exception {
        ItemDtoRequest item = ItemDtoRequest.builder()
                .name("")
                .description("Описание вещи")
                .available(true)
                .requestId(INVALID_ID)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(anyLong(), any());
    }

    @Test
    public void updateItem_WithValidRequestId_ReturnsOk() throws Exception {
        ItemDtoRequest item = ItemDtoRequest.builder()
                .name("Тестовая вещь")
                .description("Описание вещи")
                .available(false)
                .requestId(2L)
                .build();

        mvc.perform(patch("/items/{itemId}", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).updateItem(eq(VALID_USER_ID), eq(VALID_ITEM_ID), any(ItemUpdateDtoRequest.class));
    }

    @Test
    public void updateItem_WithNegativeRequestId_ReturnsBadRequest() throws Exception {
        ItemDtoRequest item = ItemDtoRequest.builder()
                .name("Тестовая вещь")
                .description("Описание вещи")
                .available(false)
                .requestId(INVALID_ID)
                .build();

        mvc.perform(patch("/items/{itemId}", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(anyLong(), anyLong(), any());
    }

    @Test
    public void deleteItem_WithValidParams_ReturnsOk() throws Exception {
        mvc.perform(delete("/items/{itemId}", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemClient).deleteItem(eq(VALID_USER_ID), eq(VALID_ITEM_ID));
    }

    @Test
    public void deleteItem_WithNegativeItemId_ReturnsBadRequest() throws Exception {
        mvc.perform(delete("/items/{itemId}", INVALID_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).deleteItem(anyLong(), anyLong());
    }

    @Test
    public void deleteItem_WithNegativeUserId_ReturnsBadRequest() throws Exception {
        mvc.perform(delete("/items/{itemId}", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, INVALID_ID))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).deleteItem(anyLong(), anyLong());
    }

    @Test
    public void addComment_WithValidData_ReturnsCreated() throws Exception {
        CommentDtoRequest comment = CommentDtoRequest.builder()
                .text("Текст отзыва")
                .build();

        mvc.perform(post("/items/{itemId}/comment", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(itemClient).addComment(eq(VALID_USER_ID), eq(VALID_ITEM_ID), any(CommentDtoRequest.class));
    }

    @Test
    public void addComment_WithEmptyText_ReturnsBadRequest() throws Exception {
        CommentDtoRequest comment = CommentDtoRequest.builder()
                .text("")
                .build();

        mvc.perform(post("/items/{itemId}/comment", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }

    @Test
    public void getComments_WithValidParams_ReturnsOk() throws Exception {
        mvc.perform(get("/items/{itemId}/comment", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemClient).getAllComments(eq(VALID_USER_ID), eq(VALID_ITEM_ID));
    }

    @Test
    public void getComments_WithNegativeItemId_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/items/{itemId}/comment", INVALID_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllComments(anyLong(), anyLong());
    }

    @Test
    public void getComments_WithNegativeUserId_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/items/{itemId}/comment", VALID_ITEM_ID)
                        .header(X_SHARER_USER_ID, INVALID_ID))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllComments(anyLong(), anyLong());
    }

    // Вспомогательный метод для настройки мок-объектов
    private void setupDefaultSuccessfulMocks() {
        when(itemClient.getAllItemsByOwner(anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(itemClient.searchByText(anyLong(), any(String.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(itemClient.createItem(anyLong(), any(ItemDtoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemUpdateDtoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentDtoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
        when(itemClient.getAllComments(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        // Для void метода deleteItem
        Mockito.doNothing().when(itemClient).deleteItem(anyLong(), anyLong());
    }
}