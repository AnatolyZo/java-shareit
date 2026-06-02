package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String DEFAULT_BOOKING_STATE = "ALL";

    @PostMapping
    public BookingDtoResponse create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approve(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId,
                              @RequestParam(name = "approved") boolean state) {
        return bookingService.approveBooking(userId, bookingId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllUsersBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "state", defaultValue = DEFAULT_BOOKING_STATE) String state) {
        return bookingService.getAllUsersBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllOwnersBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "state", defaultValue = DEFAULT_BOOKING_STATE) String state) {
        return bookingService.getAllOwnersBookings(userId, state);
    }
}
