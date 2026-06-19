package ru.practicum.shareit.booking.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;

@Slf4j
public class BookingMapper {
    public static Booking mapToBooking(BookingDto bookingDto, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setCreated(LocalDateTime.now());
        booking.setStatus(status);

        log.debug("Бронирование класса BookingDto {} преобразовано в объект класса Booking {}", bookingDto, booking);
        return booking;
    }

    public static BookingDtoResponse mapToBookingDtoResponse(Booking booking) {
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse();
        ItemDtoResponse item = ItemMapper.mapToItemDtoResponse(booking.getItem());
        UserDtoResponse booker = UserMapper.mapToUserDtoResponse(booking.getBooker());

        bookingDtoResponse.setId(booking.getId());
        bookingDtoResponse.setItem(item);
        bookingDtoResponse.setBooker(booker);
        bookingDtoResponse.setStart(booking.getStart());
        bookingDtoResponse.setEnd(booking.getEnd());
        bookingDtoResponse.setCreated(booking.getCreated());
        bookingDtoResponse.setStatus(booking.getStatus());

        log.debug("Бронирование класса Booking {} преобразовано в объект класса BookingDtoResponse {}", booking, bookingDtoResponse);
        return bookingDtoResponse;
    }
}
