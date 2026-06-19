package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidValueException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntityExistsValidationService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    public Booking getBookingByIdOrThrow(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.info("Бронирование с id {} отсутствует", bookingId);
                    return new NotFoundException(String.format("Бронирование с id %d отсутствует", bookingId));
                });
    }

    public User getUserByIdOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("Пользователь с id {} отсутствует", userId);
                    return new NotFoundException(String.format("Пользователь с id %d отсутствует", userId));
                });
    }

    public Item getItemByIdOrThrow(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.info("Вещь с id {} отсутствует", itemId);
                    return new NotFoundException(String.format("Вещь с id %d отсутствует", itemId));
                });
    }

    public ItemRequest getItemRequestByIdOrThrow(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.info("Запрос на вещь с id {} отсутствует", requestId);
                    return new NotFoundException(String.format("Запрос на вещь с id %d отсутствует", requestId));
                });
    }

    public void checkUserHasCompletedBooking(long authorId, long itemId) {
        LocalDateTime now = LocalDateTime.now();
        boolean isExistsCompletedBookings = bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(authorId, itemId, now);

        if (!isExistsCompletedBookings) {
            log.warn("Пользователь с id {} не имеет завершенных бронирований предмета с id {}, комментирование запрещено", authorId, itemId);
            throw new InvalidValueException(String.format("Пользователь с id %d не имеет завершенных бронирований предмета с id %d, комментирование запрещено", authorId, itemId));
        }
    }
}
