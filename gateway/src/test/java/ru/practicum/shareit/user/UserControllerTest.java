package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.GatewayErrorHandler;
import ru.practicum.shareit.user.dto.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GatewayErrorHandler.class)
public class UserControllerTest {

    private final String url = "/users";
    private static final Long VALID_USER_ID = 1L;
    private static final Long INVALID_ID = -1L;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserClient userClient;

    @BeforeEach
    public void setUo() {
        Mockito.reset(userClient);
        setupDefaultSuccessfulMocks();
    }

    @Test
    public void getAllUsers_ReturnsOk() throws Exception {
        mvc.perform(get(url))
                .andExpect(status().isOk());

        verify(userClient).getAllUsers();
    }

    @Test
    public void getByUserId_WithValidId_ReturnsOk() throws Exception {
        mvc.perform(get("/users/{userId}", VALID_USER_ID))
                .andExpect(status().isOk());

        verify(userClient).getUserById(eq(VALID_USER_ID));
    }

    @Test
    public void getByUserId_WithNegativeId_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/users/{userId}", INVALID_ID))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).getUserById(anyLong());
    }

    @Test
    public void getByUserId_WithZeroId_ReturnsBadRequest() throws Exception {
        mvc.perform(get("/users/{userId}", 0L))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).getUserById(anyLong());
    }

    @Test
    public void createUser_WithValidData_ReturnsCreated() throws Exception {
        UserDtoRequest user = UserDtoRequest
                .builder()
                .name("Имя")
                .email("name@yandex.ru")
                .build();

        mvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(userClient).createUser(any(UserDtoRequest.class));
    }

    @Test
    public void createUser_WithEmptyName_ReturnsBadRequest() throws Exception {
        UserDtoRequest user = UserDtoRequest
                .builder()
                .name("")
                .email("name@yandex.ru")
                .build();

        mvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }

    @Test
    public void createUser_WithInvalidEmail_ReturnsBadRequest() throws Exception {
        UserDtoRequest user = UserDtoRequest
                .builder()
                .name("Имя")
                .email("email-yandex")
                .build();

        mvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }

    @Test
    public void createUser_WithoutEmail_ReturnsBadRequest() throws Exception {
        UserDtoRequest user = UserDtoRequest
                .builder()
                .name("Имя")
                .email(null)
                .build();

        mvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }

    @Test
    public void updateUser_WithValidData_ReturnsOk() throws Exception {
        UserDtoRequest user = UserDtoRequest
                .builder()
                .name("Имя")
                .email("name@yandex.ru")
                .build();

        mvc.perform(patch("/users/{userId}", VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(VALID_USER_ID), any(UserUpdateDtoRequest.class));
    }

    @Test
    public void updateUser_WithNegativeId_ReturnsBadRequest() throws Exception {
        UserDtoRequest user = UserDtoRequest
                .builder()
                .name("Имя")
                .email("name@yandex.ru")
                .build();

        mvc.perform(patch("/users/{userId}", INVALID_ID)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any());
    }

    @Test
    public void updateUser_WithInvalidEmail_ReturnsBadRequest() throws Exception {
        UserDtoRequest user = UserDtoRequest
                .builder()
                .name("Имя")
                .email("email-yandex")
                .build();

        mvc.perform(patch("/users/{userId}", VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any());
    }

    @Test
    public void deleteUser_WithValidId_ReturnsOk() throws Exception {
        mvc.perform(delete("/users/{userId}", VALID_USER_ID))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(eq(VALID_USER_ID));
    }

    @Test
    public void deleteUser_WithNegativeId_ReturnsBadRequest() throws Exception {
        mvc.perform(delete("/users/{userId}", INVALID_ID))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).deleteUser(anyLong());
    }

    @Test
    public void deleteUser_WithZeroId_ReturnsBadRequest() throws Exception {
        mvc.perform(delete("/users/{userId}", 0L))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).deleteUser(anyLong());
    }

    private void setupDefaultSuccessfulMocks() {
        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(userClient.getUserById(anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(userClient.createUser(any(UserDtoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
        when(userClient.updateUser(anyLong(), any(UserUpdateDtoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        // Для void метода deleteUser
        when(userClient.deleteUser(anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
    }
}