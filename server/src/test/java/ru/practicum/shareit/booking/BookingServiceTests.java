package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.InvalidValueException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.validation.EntityExistsValidationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EntityExistsValidationService entityExistsValidationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking() {
        long userId = 1L;
        BookingDto request = formBookingDto();
        User booker = formBooker();
        Item item = formItem();
        LocalDateTime created = LocalDateTime.now();
        Booking savedBooking = formBooking(item, booker, created);
        BookingDtoResponse expectedBooking = formExpectedBooking(created);

        when(entityExistsValidationService.getUserByIdOrThrow(userId)).thenReturn(booker);
        when(entityExistsValidationService.getItemByIdOrThrow(request.getItemId())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingDtoResponse result = bookingService.createBooking(userId, request);

        assertThat(result, equalTo(expectedBooking));
    }

    @Test
    void createBookingWithNotExistentUser() {
        long userId = 999L;
        BookingDto request = formBookingDto();

        when(entityExistsValidationService.getUserByIdOrThrow(userId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id %d отсутствует", userId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(userId, request));

        assertEquals(String.format("Пользователь с id %d отсутствует", userId), exception.getMessage());
    }

    @Test
    void createBookingWithNotExistentItem() {
        long userId = 1L;
        BookingDto request = formBookingDto();

        when(entityExistsValidationService.getItemByIdOrThrow(request.getItemId()))
                .thenThrow(new NotFoundException(String.format("Вещь с id %d отсутствует", request.getItemId())));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(userId, request));

        assertEquals(String.format("Вещь с id %d отсутствует", request.getItemId()), exception.getMessage());
    }

    @Test
    void createBookingWithUnavailableItem() {
        long userId = 1L;
        BookingDto request = formBookingDto();
        User booker = formBooker();
        Item item = formItem();
        item.setAvailableForRent(false);

        when(entityExistsValidationService.getUserByIdOrThrow(userId)).thenReturn(booker);
        when(entityExistsValidationService.getItemByIdOrThrow(request.getItemId())).thenReturn(item);

        final InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> bookingService.createBooking(userId, request));

        assertEquals("Предмет недоступен для бронирования", exception.getMessage());
    }

    @Test
    void approveBooking() {
        long userId = 1L;
        long bookingId = 1L;
        boolean state = true;
        LocalDateTime created = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), created);
        BookingDtoResponse expectedBooking = formExpectedBooking(created);
        expectedBooking.setStatus(BookingStatus.APPROVED);

        when(entityExistsValidationService.getBookingByIdOrThrow(bookingId)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse result = bookingService.approveBooking(userId, bookingId, state);

        assertThat(result, equalTo(expectedBooking));
    }

    @Test
    void rejectBooking() {
        long userId = 1L;
        long bookingId = 1L;
        boolean state = false;
        LocalDateTime created = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), created);
        BookingDtoResponse expectedBooking = formExpectedBooking(created);
        expectedBooking.setStatus(BookingStatus.REJECTED);

        when(entityExistsValidationService.getBookingByIdOrThrow(bookingId)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse result = bookingService.approveBooking(userId, bookingId, state);

        assertThat(result, equalTo(expectedBooking));
    }

    @Test
    void approveNotExistentBooking() {
        long userId = 1L;
        long bookingId = 2L;
        boolean state = true;

        when(entityExistsValidationService.getBookingByIdOrThrow(bookingId))
                .thenThrow(new NotFoundException(String.format("Бронирование с id %d отсутствует", bookingId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(userId, bookingId, state));

        assertEquals(String.format("Бронирование с id %d отсутствует", bookingId), exception.getMessage());
    }

    @Test
    void approveBookingWithWrongUser() {
        long userId = 3L;
        long bookingId = 1L;
        boolean state = true;
        LocalDateTime created = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), created);

        when(entityExistsValidationService.getBookingByIdOrThrow(bookingId)).thenReturn(booking);

        final InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> bookingService.approveBooking(userId, bookingId, state));

        assertEquals("Подтверждать бронирование может исключительно владелец предмета", exception.getMessage());
    }

    @Test
    void getBookingById() {
        long userId = 1L;
        long bookingId = 1L;
        LocalDateTime created = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), created);
        BookingDtoResponse expectedBooking = formExpectedBooking(created);
        when(entityExistsValidationService.getBookingByIdOrThrow(bookingId)).thenReturn(booking);

        BookingDtoResponse result = bookingService.getBookingById(userId, bookingId);

        assertThat(result, equalTo(expectedBooking));
    }

    @Test
    void getNotExistentBookingById() {
        long userId = 1L;
        long bookingId = 7L;

        when(entityExistsValidationService.getBookingByIdOrThrow(bookingId))
                .thenThrow(new NotFoundException(String.format("Бронирование с id %d отсутствует", bookingId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userId, bookingId));

        assertEquals(String.format("Бронирование с id %d отсутствует", bookingId), exception.getMessage());
    }

    @Test
    void getAllUsersBookings() {
        long bookerId = 2L;
        String state = "WAITING";
        LocalDateTime created = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), created));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(created));
        when(bookingRepository.findByBookerIdAndStatusOrderByCreatedDesc(bookerId, BookingStatus.WAITING))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllUsersBookings(bookerId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllOwnersBookings() {
        long userId = 1L;
        String state = "FUTURE";
        LocalDateTime created = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), created));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(created));
        when(bookingRepository.findByItem_OwnerIdAndStartIsAfterOrderByCreatedDesc(eq(userId), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllOwnersBookings(userId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    private BookingDto formBookingDto() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3,12, 0,0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        return bookingDto;
    }

    private User formBooker() {
        User booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@email.ru");

        return booker;
    }

    private Item formItem() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@email.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailableForRent(true);

        return item;
    }

    private Booking formBooking(Item item, User booker, LocalDateTime created) {
        LocalDateTime start = LocalDateTime.of(2026, 7, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3,12, 0,0);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setCreated(created);
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    private BookingDtoResponse formExpectedBooking(LocalDateTime created) {
        LocalDateTime start = LocalDateTime.of(2026, 7, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3,12, 0,0);

        UserDtoResponse owner = new UserDtoResponse();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@email.ru");

        ItemDtoResponse item = new ItemDtoResponse();
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailableForRent(true);

        UserDtoResponse booker = new UserDtoResponse();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@email.ru");

        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse();
        bookingDtoResponse.setId(1L);
        bookingDtoResponse.setItem(item);
        bookingDtoResponse.setBooker(booker);
        bookingDtoResponse.setStart(start);
        bookingDtoResponse.setEnd(end);
        bookingDtoResponse.setCreated(created);
        bookingDtoResponse.setStatus(BookingStatus.WAITING);

        return bookingDtoResponse;
    }
}
