package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingServerController.class)
@Import(ErrorHandler.class)
public class BookingServerControllerTest {

    private final String url = "/bookings";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final Long VALID_USER_ID = 1L;
    private static final Long VALID_ITEM_ID = 1L;
    private static final Long VALID_BOOKING_ID = 1L;
    private static final Instant START = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final Instant END = Instant.now().plus(2, ChronoUnit.DAYS);

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;

    private BookingNewDto bookingNewDto;

    @BeforeEach
    public void setUp() {
        Mockito.reset(bookingService);

        ItemDto itemDto = ItemDto.builder()
                .id(VALID_ITEM_ID)
                .name("Тестовая вещь")
                .description("Описание")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .id(VALID_USER_ID)
                .name("Тестовый пользователь")
                .email("test@yandex.ru")
                .build();

        bookingDto = BookingDto.builder()
                .id(VALID_BOOKING_ID)
                .start(START)
                .end(END)
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.WAITING)
                .build();

        bookingNewDto = new BookingNewDto(VALID_ITEM_ID, START, END);
        setupDefaultSuccessfulMocks();
    }

    // Тест получения бронирования по id
    @Test
    public void findById_shouldReturnBooking() throws Exception {
        mvc.perform(get("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(bookingService).findById(VALID_USER_ID, VALID_BOOKING_ID);
    }

    // Тест получения бронирования с несуществующим id
    @Test
    public void findById_shouldReturnNotFound_whenBookingNotExists() throws Exception {
        when(bookingService.findById(VALID_USER_ID, VALID_BOOKING_ID))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mvc.perform(get("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isNotFound());

        verify(bookingService).findById(VALID_USER_ID, VALID_BOOKING_ID);
    }

    // Тест успешного создания бронирования
    @Test
    public void create_shouldCreateBooking() throws Exception {
        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingNewDto)))
                .andExpect(status().isCreated());

        verify(bookingService).create(eq(VALID_USER_ID), any(BookingNewDto.class));
    }

    // Тест создания брони, если пользователь не существует
    @Test
    public void create_shouldReturnNotFound_whenUserNotExists() throws Exception {
        when(bookingService.create(eq(VALID_USER_ID), any(BookingNewDto.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingNewDto)))
                .andExpect(status().isNotFound());

        verify(bookingService).create(eq(VALID_USER_ID), any(BookingNewDto.class));
    }

    // Тест создания брони для несуществующей вещи
    @Test
    public void create_shouldReturnNotFound_whenItemNotExists() throws Exception {
        when(bookingService.create(eq(VALID_USER_ID), any(BookingNewDto.class)))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingNewDto)))
                .andExpect(status().isNotFound());

        verify(bookingService).create(eq(VALID_USER_ID), any(BookingNewDto.class));
    }

    // Тест подтверждения брони
    @Test
    public void approve_shouldApproveBooking() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .param("approved", "true")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).approve(VALID_USER_ID, VALID_BOOKING_ID, true);
    }

    // Тест отклонения брони
    @Test
    public void approve_shouldRejectBooking() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .param("approved", "false")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(bookingService).approve(VALID_USER_ID, VALID_BOOKING_ID, false);
    }

    // Тест брони с невалидным параметром
    @Test
    public void approve_shouldReturnBadRequest_whenApprovedParamIsInvalid() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .param("approved", "invalid")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approve(anyLong(), anyLong(), anyBoolean());
    }

    // Тест бронирования с несуществующим id
    @Test
    public void approve_shouldReturnNotFound_whenBookingNotExists() throws Exception {
        when(bookingService.approve(VALID_USER_ID, VALID_BOOKING_ID, true))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .param("approved", "true")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isNotFound());

        verify(bookingService).approve(VALID_USER_ID, VALID_BOOKING_ID, true);
    }

    // Тест получения бронирований по id владельца
    @Test
    public void findByOwnerId_shouldReturnOwnerBookings() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(bookingService).findByOwnerId(VALID_USER_ID, BookingState.ALL);
    }

    // Тест поиска бронирований владельца, когда у пользователя отсутствуют бронирования
    @Test
    public void findByOwnerId_shouldReturnEmptyList_whenNoBookings() throws Exception {
        setupEmptyListMocks();

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(bookingService).findByOwnerId(VALID_USER_ID, BookingState.ALL);
    }

    // Тест получения брони владельца с некорректным состоянием бронирования
    @Test
    public void findByOwnerId_shouldReturnBadRequest_whenStateIsInvalid() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "INVALID_STATE")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByOwnerId(anyLong(), any());
    }

    // Тест получения бронирований по id пользователя
    @Test
    public void findByBookerId_shouldReturnUserBookings() throws Exception {
        mvc.perform(get(url)
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isOk());

        verify(bookingService).findByBookerId(VALID_USER_ID, BookingState.ALL);
    }

    // Тест получения брони пользователя с некорректным состоянием бронирования
    @Test
    public void findByBookerId_shouldReturnBadRequest_whenStateIsInvalid() throws Exception {
        mvc.perform(get(url)
                        .param("state", "INVALID_STATE")
                        .header(X_SHARER_USER_ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByBookerId(anyLong(), any());
    }

    // Вспомогательные методы для настройки мок-объектов
    private void setupDefaultSuccessfulMocks() {
        when(bookingService.findById(VALID_USER_ID, VALID_BOOKING_ID))
                .thenReturn(bookingDto);
        when(bookingService.create(eq(VALID_USER_ID), any(BookingNewDto.class)))
                .thenReturn(bookingDto);
        when(bookingService.approve(VALID_USER_ID, VALID_BOOKING_ID, true))
                .thenReturn(bookingDto.toBuilder().status(BookingStatus.APPROVED).build());
        when(bookingService.approve(VALID_USER_ID, VALID_BOOKING_ID, false))
                .thenReturn(bookingDto.toBuilder().status(BookingStatus.REJECTED).build());
        when(bookingService.findByOwnerId(eq(VALID_USER_ID), any(BookingState.class)))
                .thenReturn(List.of(bookingDto));
        when(bookingService.findByBookerId(eq(VALID_USER_ID), any(BookingState.class)))
                .thenReturn(List.of(bookingDto));
    }

    private void setupEmptyListMocks() {
        when(bookingService.findByOwnerId(eq(VALID_USER_ID), any(BookingState.class)))
                .thenReturn(List.of());
        when(bookingService.findByBookerId(eq(VALID_USER_ID), any(BookingState.class)))
                .thenReturn(List.of());
    }
}