package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@Import(GatewayErrorHandler.class)
public class ItemRequestControllerTest {
    private final String url = "/requests";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final Long VALID_USER_ID = 1L;
    private static final Long VALID_REQUEST_ID = 1L;
    private static final Long INVALID_ID = -1L;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestClient itemRequestClient;

    @BeforeEach
    public void setUo() {
        Mockito.reset(itemRequestClient);
        setupDefaultSuccessfulMocks();
    }

    @Test
    public void createRequest_WithValidData_ReturnsCreated() throws Exception {
        ItemRequestDtoRequest request = ItemRequestDtoRequest
                .builder()
                .description("Описание запроса")
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(itemRequestClient).createRequest(eq(VALID_USER_ID), any(ItemRequestDtoRequest.class));
    }

    @Test
    public void createRequest_WithEmptyDescription_ReturnsBadRequest() throws Exception {
        ItemRequestDtoRequest request = ItemRequestDtoRequest
                .builder()
                .description("")
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createRequest(anyLong(), any());
    }

    @Test
    public void createRequest_WithNullDescription_ReturnsBadRequest() throws Exception {
        ItemRequestDtoRequest request = ItemRequestDtoRequest
                .builder()
                .description(null)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createRequest(anyLong(), any());
    }

    @Test
    public void createRequest_WithNegativeUserId_ReturnsBadRequest() throws Exception {
        ItemRequestDtoRequest request = ItemRequestDtoRequest
                .builder()
                .description("Описание запроса")
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, INVALID_ID)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createRequest(anyLong(), any());
    }

    @Test
    public void createRequest_WithoutUserIdHeader_ReturnsBadRequest() throws Exception {
        ItemRequestDtoRequest request = ItemRequestDtoRequest
                .builder()
                .description("Описание запроса")
                .build();

        mvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createRequest(anyLong(), any());
    }

    @Test
    public void getAllRequests_WithValidUserId_ReturnsOk() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(eq(VALID_USER_ID));
    }

    @Test
    public void getAllRequests_WithNegativeUserId_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, INVALID_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(anyLong());
    }

    @Test
    public void getAllRequests_WithoutUserIdHeader_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(anyLong());
    }

    @Test
    public void getRequestByUser_WithValidUserId_ReturnsOk() throws Exception {
        mvc.perform(get(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient).getRequestByUser(eq(VALID_USER_ID));
    }

    @Test
    public void getRequestByUser_WithNegativeUserId_ReturnsBadRequest() throws Exception {
        mvc.perform(get(url)
                        .header(X_SHARER_USER_ID, INVALID_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestByUser(anyLong());
    }

    @Test
    public void getRequestByUser_WithoutUserIdHeader_ReturnsBadRequest() throws Exception {
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestByUser(anyLong());
    }

    @Test
    public void getRequestById_WithValidParams_ReturnsOk() throws Exception {
        mvc.perform(get("/requests/{requestId}", VALID_REQUEST_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient).getRequestById(eq(VALID_USER_ID), eq(VALID_REQUEST_ID));
    }

    @Test
    public void getRequestById_WithNegativeRequestId_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/requests/{requestId}", INVALID_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestById(anyLong(), anyLong());
    }

    @Test
    public void getRequestById_WithNegativeUserId_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/requests/{requestId}", VALID_REQUEST_ID)
                        .header(X_SHARER_USER_ID, INVALID_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestById(anyLong(), anyLong());
    }

    @Test
    public void getRequestById_WithoutUserIdHeader_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/requests/{requestId}", VALID_REQUEST_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestById(anyLong(), anyLong());
    }

    // Вспомогательный метод для настройки мок-объектов
    private void setupDefaultSuccessfulMocks() {
        when(itemRequestClient.createRequest(anyLong(), any(ItemRequestDtoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
        when(itemRequestClient.getAllRequests(anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(itemRequestClient.getRequestByUser(anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(itemRequestClient.getRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
    }
}