package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exceptions.InvalidValueException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.EntityExistsValidationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final EntityExistsValidationService entityExistsValidationService;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(long userId, BookingDto bookingDto) {
        log.trace("Пользователь с id {} инициировал бронирование: {}", userId, bookingDto);
        User booker = entityExistsValidationService.getUserByIdOrThrow(userId);
        Item item = entityExistsValidationService.getItemByIdOrThrow(bookingDto.getItemId());

        if (!item.isAvailableForRent()) {
            throw new InvalidValueException("Предмет недоступен для бронирования");
        }

        Booking booking = BookingMapper.mapToBooking(bookingDto, item, booker, BookingStatus.WAITING);
        Booking createdBooking = bookingRepository.save(booking);
        log.debug("В хранилище добавлено бронирование {}", createdBooking);
        return BookingMapper.mapToBookingDtoResponse(createdBooking);
    }

    @Override
    @Transactional
    public BookingDtoResponse approveBooking(long userId, long bookingId, boolean state) {
        log.trace("Пользователь с id {} инициировал подтверждение бронирования предмета с id {}, параметр state = {}", userId, bookingId, state);
        Booking booking = entityExistsValidationService.getBookingByIdOrThrow(bookingId);

        if (userId != booking.getItem().getOwner().getId()) {
            log.warn("Пользователь с id {} не является владельцем предмета с id {}", userId, bookingId);
            throw new InvalidValueException("Подтверждать бронирование может исключительно владелец предмета");
        }

        if (state) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking approvedBooking = bookingRepository.save(booking);
        log.debug("В хранилище обновлено бронирование {} после выполнения запроса на подтверждение", approvedBooking);
        return BookingMapper.mapToBookingDtoResponse(approvedBooking);
    }

    @Override
    public BookingDtoResponse getBookingById(long userId, long bookingId) {
        log.trace("Пользователь с id {} инициировал получение бронирования с id {}", userId, bookingId);
        Booking booking = entityExistsValidationService.getBookingByIdOrThrow(bookingId);

        if (userId != booking.getItem().getOwner().getId() && userId != booking.getBooker().getId()) {
            log.warn("Пользователь с id {} не является автор бронирования или владелец предмета с id {}", userId, bookingId);
            throw new InvalidValueException("Получить информацию о бронировании может либо автор бронирования, либо владелец предмета");
        }

        return BookingMapper.mapToBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllUsersBookings(long bookerId, String state) {
        log.trace("Пользователь с id {} инициировал получение всех иницированных им бронирований с критерием отбора \"{}\"", bookerId, state);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        if (state.equals(BookingSearchValues.ALL.name())) {
            bookings = bookingRepository.findByBookerIdOrderByCreatedDesc(bookerId);
        } else if (state.equals(BookingSearchValues.CURRENT.name())) {
            bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(bookerId, now);
        } else if (state.equals(BookingSearchValues.PAST.name())) {
            bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByCreatedDesc(bookerId, now);
        } else if (state.equals(BookingSearchValues.FUTURE.name())) {
            bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByCreatedDesc(bookerId, now);
        } else if (state.equals(BookingSearchValues.WAITING.name())) {
            bookings = bookingRepository.findByBookerIdAndStatusOrderByCreatedDesc(bookerId, BookingStatus.WAITING);
        } else if (state.equals(BookingSearchValues.REJECTED.name())) {
            bookings = bookingRepository.findByBookerIdAndStatusOrderByCreatedDesc(bookerId, BookingStatus.REJECTED);
        } else {
            throw new InvalidValueException(String.format("Недопустимый параметр поиска - %s", state));
        }

        log.debug("Результат получения бронирований из БД: {}", bookings);

        return bookings.stream()
                .map(BookingMapper::mapToBookingDtoResponse)
                .toList();
    }

    @Override
    public List<BookingDtoResponse> getAllOwnersBookings(long ownerId, String state) {
        log.trace("Пользователь с id {} инициировал получение бронирований всех своих предметов с критерием отбора \"{}\"", ownerId, state);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        entityExistsValidationService.getUserByIdOrThrow(ownerId);

        if (state.equals(BookingSearchValues.ALL.name())) {
            bookings = bookingRepository.findByItem_OwnerIdOrderByCreatedDesc(ownerId);
        } else if (state.equals(BookingSearchValues.CURRENT.name())) {
            bookings = bookingRepository.findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, now);
        } else if (state.equals(BookingSearchValues.PAST.name())) {
            bookings = bookingRepository.findByItem_OwnerIdAndEndIsBeforeOrderByCreatedDesc(ownerId, now);
        } else if (state.equals(BookingSearchValues.FUTURE.name())) {
            bookings = bookingRepository.findByItem_OwnerIdAndStartIsAfterOrderByCreatedDesc(ownerId, now);
        } else if (state.equals(BookingSearchValues.WAITING.name())) {
            bookings = bookingRepository.findByItem_OwnerIdAndStatusOrderByCreatedDesc(ownerId, BookingStatus.WAITING);
        } else if (state.equals(BookingSearchValues.REJECTED.name())) {
            bookings = bookingRepository.findByItem_OwnerIdAndStatusOrderByCreatedDesc(ownerId, BookingStatus.REJECTED);
        } else {
            throw new InvalidValueException(String.format("Недопустимый параметр поиска - %s", state));
        }

        log.debug("Результат получения бронирований предметов владельца с id {} из БД: {}", ownerId, bookings);

        return bookings.stream()
                .map(BookingMapper::mapToBookingDtoResponse)
                .toList();
    }
}
