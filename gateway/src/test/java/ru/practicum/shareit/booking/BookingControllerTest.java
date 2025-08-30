package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.exception.GatewayErrorHandler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import(GatewayErrorHandler.class)
public class BookingControllerTest {

    private final String url = "/bookings";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final Long VALID_USER_ID = 1L;
    private static final Long VALID_ITEM_ID = 1L;
    private static final Long VALID_BOOKING_ID = 1L;
    private static final Long INVALID_ID = -1L;
    private static final Instant TOMORROW = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final Instant DAY_AFTER_TOMORROW = Instant.now().plus(2, ChronoUnit.DAYS);

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @BeforeEach
    public void setUo() {
        Mockito.reset(bookingClient);
        setupDefaultSuccessfulMocks();
    }

    @Test
    public void createBooking_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        BookingDtoRequest booking = BookingDtoRequest.builder()
                .itemId(VALID_ITEM_ID)
                .start(TOMORROW)
                .end(DAY_AFTER_TOMORROW)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(bookingClient).createBooking(eq(VALID_USER_ID), any(BookingDtoRequest.class));
    }

    @Test
    public void createBooking_ShouldReturnBadRequest_WhenItemIdIsNull() throws Exception {
        BookingDtoRequest booking = BookingDtoRequest.builder()
                .itemId(null)
                .start(TOMORROW)
                .end(DAY_AFTER_TOMORROW)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any());
    }

    @Test
    public void createBooking_ShouldReturnBadRequest_WhenItemIdIsNotPositive() throws Exception {
        BookingDtoRequest booking = BookingDtoRequest.builder()
                .itemId(0L)
                .start(Instant.now().plus(1, ChronoUnit.DAYS))
                .end(Instant.now().plus(2, ChronoUnit.DAYS))
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any());
    }

    @Test
    public void createBooking_ShouldReturnBadRequest_WhenStartIsNull() throws Exception {
        BookingDtoRequest booking = BookingDtoRequest.builder()
                .itemId(VALID_ITEM_ID)
                .start(null)
                .end(DAY_AFTER_TOMORROW)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any());
    }

    @Test
    public void createBooking_ShouldReturnBadRequest_WhenStartIsInPast() throws Exception {
        BookingDtoRequest booking = BookingDtoRequest.builder()
                .itemId(VALID_ITEM_ID)
                .start(Instant.now().minus(1, ChronoUnit.DAYS))
                .end(DAY_AFTER_TOMORROW)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any());
    }

    @Test
    public void createBooking_ShouldReturnBadRequest_WhenEndIsNull() throws Exception {
        BookingDtoRequest booking = BookingDtoRequest.builder()
                .itemId(VALID_ITEM_ID)
                .start(TOMORROW)
                .end(null)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any());
    }

    @Test
    public void createBooking_ShouldReturnBadRequest_WhenEndIsBeforeStart() throws Exception {
        BookingDtoRequest booking = BookingDtoRequest.builder()
                .itemId(VALID_ITEM_ID)
                .start(TOMORROW)
                .end(TOMORROW.minus(2, ChronoUnit.DAYS))
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any());
    }

    @Test
    public void createBooking_ShouldReturnBadRequest_WhenUserIdIsNotPositive() throws Exception {
        BookingDtoRequest booking = BookingDtoRequest.builder()
                .itemId(VALID_ITEM_ID)
                .start(TOMORROW)
                .end(DAY_AFTER_TOMORROW)
                .build();

        mvc.perform(post(url)
                        .header(X_SHARER_USER_ID, INVALID_ID)
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any());
    }

    @Test
    public void approveBooking_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .param("approved", "true")
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).approveBooking(eq(VALID_USER_ID), eq(VALID_BOOKING_ID), eq(true));
    }

    @Test
    public void approveBooking_ShouldReturnBadRequest_WhenBookingIdIsNotPositive() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", INVALID_ID)
                        .param("approved", "true")
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    public void approveBooking_ShouldReturnBadRequest_WhenUserIdIsNotPositive() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .param("approved", "true")
                        .header(X_SHARER_USER_ID, INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    public void approveBooking_ShouldReturnBadRequest_WhenApprovedParamIsInvalid() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .param("approved", "invalid")
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    public void getBooking_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        mvc.perform(get("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(eq(VALID_USER_ID), eq(VALID_BOOKING_ID));
    }

    @Test
    public void getBooking_ShouldReturnBadRequest_WhenBookingIdIsNotPositive() throws Exception {
        mvc.perform(get("/bookings/{bookingId}", INVALID_ID)
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBooking(anyLong(), anyLong());
    }

    @Test
    public void getBooking_ShouldReturnBadRequest_WhenUserIdIsNotPositive() throws Exception {
        mvc.perform(get("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .header(X_SHARER_USER_ID, INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBooking(anyLong(), anyLong());
    }

    @Test
    public void getBookingsByOwner_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingByOwner(eq(VALID_USER_ID), eq(BookingState.ALL));
    }

    @Test
    public void getBookingsByOwner_ShouldReturnBadRequest_WhenUserIdIsNotPositive() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingByOwner(anyLong(), any());
    }

    @Test
    public void getBookingsByOwner_ShouldReturnBadRequest_WhenStateIsInvalid() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "INVALID_STATE")
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingByOwner(anyLong(), any());
    }

    @Test
    public void getBookingsByBooker_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        mvc.perform(get(url)
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingByBooker(eq(VALID_USER_ID), eq(BookingState.ALL));
    }

    @Test
    public void getBookingsByBooker_ShouldReturnBadRequest_WhenUserIdIsNotPositive() throws Exception {
        mvc.perform(get(url)
                        .param("state", "ALL")
                        .header(X_SHARER_USER_ID, INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingByBooker(anyLong(), any());
    }

    @Test
    public void getBookingsByBooker_ShouldReturnBadRequest_WhenStateIsInvalid() throws Exception {
        mvc.perform(get(url)
                        .param("state", "INVALID_STATE")
                        .header(X_SHARER_USER_ID, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingByBooker(anyLong(), any());
    }

    // Вспомогательный метод для настройки мок-объектов
    private void setupDefaultSuccessfulMocks() {
        when(bookingClient.createBooking(anyLong(), any(BookingDtoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
        when(bookingClient.approveBooking(anyLong(), anyLong(), any(Boolean.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(bookingClient.getBookingByOwner(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(bookingClient.getBookingByBooker(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
    }
}