package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User testOwner;
    private User testBooker;
    private User otherUser;
    private Item availableItem;
    private Item unavailableItem;
    private Booking testBooking;

    @BeforeEach
    public void beforeEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();

        // Создание и сохранение тестового владельца
        testOwner = userRepository.save(User.builder()
                .name("Тестовый владелец")
                .email("owner@yandex.ru")
                .build());

        // Создание и сохранение арендатора
        testBooker = userRepository.save(User.builder()
                .name("Тестовый арендатор")
                .email("booker@gmail.com")
                .build());

        // Создание другого пользователя
        otherUser = userRepository.save(User.builder()
                .name("Новый пользователь")
                .email("other@yahoo.com")
                .build());

        // Создание доступной вещи
        availableItem = itemRepository.save(Item.builder()
                .name("Доступная вещь")
                .description("Описание вещи")
                .available(true)
                .owner(testOwner)
                .build());

        // Создание недоступной вещи
        unavailableItem = itemRepository.save(Item.builder()
                .name("Недоступная вещь")
                .description("Описание")
                .available(false)
                .owner(testOwner)
                .build());

        // Создание тестового бронирования
        testBooking = bookingRepository.save(Booking.builder()
                .start(Instant.now().plus(1, ChronoUnit.DAYS))
                .end(Instant.now().plus(2, ChronoUnit.DAYS))
                .item(availableItem)
                .booker(testBooker)
                .status(BookingStatus.WAITING)
                .build());
    }

    // Тест получения брони по ID для владельца вещи
    @Test
    public void findById_shouldReturnBookingForOwner() {
        BookingDto result = bookingService.findById(testOwner.getId(), testBooking.getId());

        assertNotNull(result);
        assertEquals(testBooking.getId(), result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    // Тест для получения брони по ID для арендатора
    @Test
    public void findById_shouldReturnBookingForBooker() {
        BookingDto result = bookingService.findById(testBooker.getId(), testBooking.getId());

        assertNotNull(result);
        assertEquals(testBooking.getId(), result.getId());
    }

    // Тест граничного случая получения брони по ID для пользователя без прав доступа(ForbiddenException)
    @Test
    public void findById_shouldThrowForbiddenExceptionForUnauthorizedUser() {
        assertThrows(ForbiddenException.class, () -> {
            bookingService.findById(otherUser.getId(), testBooking.getId());
        });
    }

    // Тест граничного случая получения несуществующей брони по ID(NotFoundException)
    @Test
    public void findById_shouldThrowNotFoundExceptionForNonExistentBooking() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.findById(testOwner.getId(), 999L);
        });
    }

    // Тест создания новой брони
    @Test
    public void create_shouldCreateNewBooking() {
        BookingNewDto newBookingDto = new BookingNewDto(
                availableItem.getId(),
                Instant.now().plus(3, ChronoUnit.DAYS),
                Instant.now().plus(4, ChronoUnit.DAYS)
        );

        BookingDto result = bookingService.create(testBooker.getId(), newBookingDto);

        assertNotNull(result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(availableItem.getId(), result.getItem().getId());
    }

    // Тест граничного случая создания новой брони для своей вещи(ForbiddenException)
    @Test
    public void create_shouldThrowForbiddenExceptionForOwnItem() {
        BookingNewDto newBookingDto = new BookingNewDto(
                availableItem.getId(),
                Instant.now().plus(3, ChronoUnit.DAYS),
                Instant.now().plus(4, ChronoUnit.DAYS)
        );

        assertThrows(ForbiddenException.class, () -> {
            bookingService.create(testOwner.getId(), newBookingDto);
        });
    }

    // Тест граничного случая создания новой брони для недоступной вещи(ItemNotAvailableException)
    @Test
    public void create_shouldThrowItemNotAvailableExceptionForUnavailableItem() {
        BookingNewDto newBookingDto = new BookingNewDto(
                unavailableItem.getId(),
                Instant.now().plus(3, ChronoUnit.DAYS),
                Instant.now().plus(4, ChronoUnit.DAYS)
        );

        assertThrows(ItemNotAvailableException.class, () -> {
            bookingService.create(testBooker.getId(), newBookingDto);
        });
    }

    // Тест граничного случая создания новой брони для несуществующей вещи(NotFoundException)
    @Test
    public void create_shouldThrowNotFoundExceptionForNonExistentItem() {
        BookingNewDto newBookingDto = new BookingNewDto(
                999L,
                Instant.now().plus(3, ChronoUnit.DAYS),
                Instant.now().plus(4, ChronoUnit.DAYS)
        );

        assertThrows(NotFoundException.class, () -> {
            bookingService.create(testBooker.getId(), newBookingDto);
        });
    }

    // Тест подтверждения брони владельцем
    @Test
    public void approve_shouldApproveBooking() {
        BookingDto result = bookingService.approve(testOwner.getId(), testBooking.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    // Тест отклонения брони владельцем
    @Test
    public void approve_shouldRejectBooking() {
        BookingDto result = bookingService.approve(testOwner.getId(), testBooking.getId(), false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    // Тест граничного случая подтверждения брони не владельцем вещи(ForbiddenException)
    @Test
    public void approve_shouldThrowForbiddenExceptionForNonOwner() {
        assertThrows(ForbiddenException.class, () -> {
            bookingService.approve(testBooker.getId(), testBooking.getId(), true);
        });
    }

    // Тест получения списка бронирований владельца со статусом ALL
    @Test
    public void findByOwnerId_shouldReturnAllBookingsForOwner() {
        Collection<BookingDto> result = bookingService.findByOwnerId(testOwner.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // Тест получения списка бронирований владельца со статусом WAITING
    @Test
    public void findByOwnerId_shouldReturnWaitingBookingsForOwner() {
        Collection<BookingDto> result = bookingService.findByOwnerId(testOwner.getId(), BookingState.WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.WAITING, result.iterator().next().getStatus());
    }

    // Тест граничного случая получения списка бронирований для пользователя без вещей(возврат пустого списка)
    @Test
    public void findByOwnerId_shouldReturnEmptyListForUserWithoutItems() {
        Collection<BookingDto> result = bookingService.findByOwnerId(otherUser.getId(), BookingState.ALL);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Тест получения бронирований арендатора со статусом ALL
    @Test
    public void findByBookerId_shouldReturnAllBookingsForBooker() {
        Collection<BookingDto> result = bookingService.findByBookerId(testBooker.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // Тест получения бронирований арендатора со статусом FUTURE
    @Test
    public void findByBookerId_shouldReturnFutureBookingsForBooker() {
        // создаем бронирование в будущем
        Booking futureBooking = bookingRepository.save(Booking.builder()
                .start(Instant.now().plus(3, ChronoUnit.DAYS))  // Явно в будущем
                .end(Instant.now().plus(4, ChronoUnit.DAYS))
                .item(availableItem)
                .booker(testBooker)
                .status(BookingStatus.APPROVED)
                .build());

        Collection<BookingDto> result = bookingService.findByBookerId(testBooker.getId(), BookingState.FUTURE);

        assertNotNull(result);
        assertEquals(1, result.size()); // Теперь должно найти futureBooking
        assertEquals(futureBooking.getId(), result.iterator().next().getId());
    }

    // Тест граничного случая получения пустого списка для пользователя без бронирований
    @Test
    public void findByBookerId_shouldReturnEmptyListForUserWithoutBookings() {
        Collection<BookingDto> result = bookingService.findByBookerId(otherUser.getId(), BookingState.ALL);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}