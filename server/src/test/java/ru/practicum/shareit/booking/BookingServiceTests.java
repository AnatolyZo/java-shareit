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
        LocalDateTime now = LocalDateTime.now();
        Booking savedBooking = formBooking(item, booker, now, null, null);
        BookingDtoResponse expectedBooking = formExpectedBooking(now, null, null);

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
        LocalDateTime now = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), now, null, null);
        BookingDtoResponse expectedBooking = formExpectedBooking(now, null, null);
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
        LocalDateTime now = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), now, null, null);
        BookingDtoResponse expectedBooking = formExpectedBooking(now, null, null);
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
        LocalDateTime now = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), now, null, null);

        when(entityExistsValidationService.getBookingByIdOrThrow(bookingId)).thenReturn(booking);

        final InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> bookingService.approveBooking(userId, bookingId, state));

        assertEquals("Подтверждать бронирование может исключительно владелец предмета", exception.getMessage());
    }

    @Test
    void getBookingById() {
        long userId = 1L;
        long bookingId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), now, null, null);
        BookingDtoResponse expectedBooking = formExpectedBooking(now, null, null);
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
        String state = "ALL";
        LocalDateTime created = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), created, null, null));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(created, null, null));
        when(bookingRepository.findByBookerIdOrderByCreatedDesc(bookerId))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllUsersBookings(bookerId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllUsersBookingsWithCurrentStatus() {
        long bookerId = 2L;
        String state = "CURRENT";
        LocalDateTime start = LocalDateTime.of(2026, 5, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3,12, 0,0);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), now, start, end));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(now, start, end));
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(eq(bookerId), any()))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllUsersBookings(bookerId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllUsersBookingsWithPastStatus() {
        long bookerId = 2L;
        String state = "PAST";
        LocalDateTime start = LocalDateTime.of(2026, 5, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 3,12, 0,0);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), now, start, end));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(now, start, end));
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByCreatedDesc(eq(bookerId), any()))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllUsersBookings(bookerId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllUsersBookingsWithFutureStatus() {
        long bookerId = 2L;
        String state = "FUTURE";
        LocalDateTime start = LocalDateTime.of(2026, 8, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 3,12, 0,0);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), now, start, end));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(now, start, end));
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByCreatedDesc(eq(bookerId), any()))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllUsersBookings(bookerId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllUsersBookingsWithWaitingStatus() {
        long bookerId = 2L;
        String state = "WAITING";
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), now, null, null));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(now, null, null));
        when(bookingRepository.findByBookerIdAndStatusOrderByCreatedDesc(bookerId, BookingStatus.WAITING))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllUsersBookings(bookerId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllUsersBookingsWithRejectedStatus() {
        long bookerId = 2L;
        String state = "REJECTED";
        LocalDateTime now = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), now, null, null);
        booking.setStatus(BookingStatus.REJECTED);

        BookingDtoResponse expectedBooking = formExpectedBooking(now, null, null);
        expectedBooking.setStatus(BookingStatus.REJECTED);

        List<Booking> bookings = List.of(booking);
        List<BookingDtoResponse> expectedBookings = List.of(expectedBooking);

        when(bookingRepository.findByBookerIdAndStatusOrderByCreatedDesc(bookerId, BookingStatus.REJECTED))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllUsersBookings(bookerId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllUsersBookingsWithInvalidStatus() {
        long bookerId = 2L;
        String state = "INVALID";

        final InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> bookingService.getAllUsersBookings(bookerId, state));

        assertEquals(String.format("Недопустимый параметр поиска - %s", state), exception.getMessage());
    }

    @Test
    void getAllOwnersBookings() {
        long userId = 1L;
        String state = "ALL";
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), now, null, null));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(now, null, null));
        when(bookingRepository.findByItem_OwnerIdOrderByCreatedDesc(userId))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllOwnersBookings(userId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllOwnersBookingsWithCurrentStatus() {
        long userId = 1L;
        String state = "CURRENT";
        LocalDateTime start = LocalDateTime.of(2026, 5, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3,12, 0,0);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), now, start, end));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(now, start, end));
        when(bookingRepository.findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(eq(userId), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllOwnersBookings(userId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllOwnersBookingsWithPastStatus() {
        long userId = 1L;
        String state = "PAST";
        LocalDateTime start = LocalDateTime.of(2026, 5, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 3,12, 0,0);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), now, start, end));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(now, start, end));
        when(bookingRepository.findByItem_OwnerIdAndEndIsBeforeOrderByCreatedDesc(eq(userId), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllOwnersBookings(userId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllOwnersBookingsWithFutureStatus() {
        long userId = 1L;
        String state = "FUTURE";
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), now, null, null));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(now, null, null));
        when(bookingRepository.findByItem_OwnerIdAndStartIsAfterOrderByCreatedDesc(eq(userId), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllOwnersBookings(userId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllOwnersBookingsWithWaitingStatus() {
        long userId = 1L;
        String state = "WAITING";
        LocalDateTime start = LocalDateTime.of(2026, 5, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 3,12, 0,0);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(formBooking(formItem(), formBooker(), now, start, end));
        List<BookingDtoResponse> expectedBookings = List.of(formExpectedBooking(now, start, end));
        when(bookingRepository.findByItem_OwnerIdAndStatusOrderByCreatedDesc(userId, BookingStatus.WAITING))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllOwnersBookings(userId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllOwnersBookingsWithRejectedStatus() {
        long userId = 1L;
        String state = "REJECTED";
        LocalDateTime now = LocalDateTime.now();

        Booking booking = formBooking(formItem(), formBooker(), now, null, null);
        booking.setStatus(BookingStatus.REJECTED);

        BookingDtoResponse expectedBooking = formExpectedBooking(now, null, null);
        expectedBooking.setStatus(BookingStatus.REJECTED);

        List<Booking> bookings = List.of(booking);
        List<BookingDtoResponse> expectedBookings = List.of(expectedBooking);

        when(bookingRepository.findByItem_OwnerIdAndStatusOrderByCreatedDesc(userId, BookingStatus.REJECTED))
                .thenReturn(bookings);

        List<BookingDtoResponse> result = bookingService.getAllOwnersBookings(userId, state);

        assertThat(result, equalTo(expectedBookings));
    }

    @Test
    void getAllOwnersBookingsWithInvalidStatus() {
        long userId = 1L;
        String state = "INVALID";

        final InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> bookingService.getAllOwnersBookings(userId, state));

        assertEquals(String.format("Недопустимый параметр поиска - %s", state), exception.getMessage());
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

    private Booking formBooking(Item item, User booker, LocalDateTime created, LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            start = LocalDateTime.of(2026, 7, 1,12, 0,0);
        }

        if (end == null) {
            end = LocalDateTime.of(2026, 7, 3,12, 0,0);
        }

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

    private BookingDtoResponse formExpectedBooking(LocalDateTime created, LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            start = LocalDateTime.of(2026, 7, 1,12, 0,0);
        }

        if (end == null) {
            end = LocalDateTime.of(2026, 7, 3,12, 0,0);
        }

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
