package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(long userId, BookingDto bookingDto);

    BookingDtoResponse approveBooking(long userId, long bookingId, boolean state);

    BookingDtoResponse getBookingById(long userId, long bookingId);

    List<BookingDtoResponse> getAllUsersBookings(long userId, String state);

    List<BookingDtoResponse> getAllOwnersBookings(long userId, String state);
}
