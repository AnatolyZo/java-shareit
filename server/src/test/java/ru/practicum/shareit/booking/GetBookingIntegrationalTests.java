package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.InvalidValueException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetBookingIntegrationalTests {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void createAndGetBooking() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("Owner1");
        ownerDto.setEmail("owner1@email.ru");

        UserDtoResponse owner = userService.createUser(ownerDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("Booker1");
        bookerDto.setEmail("booker1@email.ru");

        UserDtoResponse booker = userService.createUser(bookerDto);

        ItemDto availableItemDto = new ItemDto();
        availableItemDto.setName("Item1");
        availableItemDto.setDescription("Description1");
        availableItemDto.setIsAvailableForRent(true);

        ItemDtoResponse item = itemService.createItem(owner.getId(), availableItemDto);

        LocalDateTime start = LocalDateTime.of(2026, 7, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3,12, 0,0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        BookingDtoResponse createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        BookingDtoResponse retrievedBooking = bookingService.getBookingById(owner.getId(), createdBooking.getId());

        BookingDtoResponse expectedBooking = new BookingDtoResponse();
        expectedBooking.setId(createdBooking.getId());
        expectedBooking.setItem(item);
        expectedBooking.setBooker(booker);
        expectedBooking.setStart(start);
        expectedBooking.setEnd(end);
        expectedBooking.setCreated(createdBooking.getCreated());
        expectedBooking.setStatus(BookingStatus.WAITING);

        assertThat(retrievedBooking, equalTo(expectedBooking));
    }

    @Test
    void getNotExistentBooking() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("Owner2");
        ownerDto.setEmail("owner2@email.ru");

        UserDtoResponse owner = userService.createUser(ownerDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("Booker2");
        bookerDto.setEmail("booker2@email.ru");

        UserDtoResponse booker = userService.createUser(bookerDto);

        ItemDto availableItemDto = new ItemDto();
        availableItemDto.setName("Item2");
        availableItemDto.setDescription("Description2");
        availableItemDto.setIsAvailableForRent(true);

        ItemDtoResponse item = itemService.createItem(owner.getId(), availableItemDto);

        LocalDateTime start = LocalDateTime.of(2026, 7, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3,12, 0,0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        BookingDtoResponse createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        //Пытаемся получить броонирование со следующим id относительно последнего созданного
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(booker.getId(), createdBooking.getId() + 1));

        assertEquals(String.format("Бронирование с id %d отсутствует", createdBooking.getId() + 1), exception.getMessage());
    }

    @Test
    void getBookingByWrongUser() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("Owner3");
        ownerDto.setEmail("owner3@email.ru");

        UserDtoResponse owner = userService.createUser(ownerDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("Booker3");
        bookerDto.setEmail("booker3@email.ru");

        UserDtoResponse booker = userService.createUser(bookerDto);

        UserDto otherUserDto = new UserDto();
        otherUserDto.setName("OtherUser1");
        otherUserDto.setEmail("other_user1@email.ru");

        UserDtoResponse notOwnerOrBookerUser = userService.createUser(otherUserDto);

        ItemDto availableItemDto = new ItemDto();
        availableItemDto.setName("Item3");
        availableItemDto.setDescription("Description3");
        availableItemDto.setIsAvailableForRent(true);

        ItemDtoResponse item = itemService.createItem(owner.getId(), availableItemDto);

        LocalDateTime start = LocalDateTime.of(2026, 7, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3,12, 0,0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        BookingDtoResponse createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        //Пытаемся получить бронирование пользователя, который не является владельцем или арендатором
        final InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> bookingService.getBookingById(notOwnerOrBookerUser.getId(), createdBooking.getId()));

        assertEquals("Получить информацию о бронировании может либо автор бронирования, либо владелец предмета", exception.getMessage());
    }

    @Test
    void getFailedToCreateBooking() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("Owner4");
        ownerDto.setEmail("owner4@email.ru");

        UserDtoResponse owner = userService.createUser(ownerDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("Booker4");
        bookerDto.setEmail("booker4@email.ru");

        UserDtoResponse booker = userService.createUser(bookerDto);

        ItemDto availableItemDto = new ItemDto();
        availableItemDto.setName("Item4");
        availableItemDto.setDescription("Description4");
        availableItemDto.setIsAvailableForRent(true);

        ItemDtoResponse availableItem = itemService.createItem(owner.getId(), availableItemDto);

        ItemDto unavailableItemDto = new ItemDto();
        unavailableItemDto.setName("Item5");
        unavailableItemDto.setDescription("Description5");
        unavailableItemDto.setIsAvailableForRent(false);

        ItemDtoResponse unavailableItem = itemService.createItem(owner.getId(), unavailableItemDto);

        LocalDateTime start = LocalDateTime.of(2026, 7, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3,12, 0,0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(availableItem.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        BookingDtoResponse createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        BookingDto bookingNotExistentItemDto = new BookingDto();
        bookingNotExistentItemDto.setItemId(unavailableItem.getId() + 1);
        bookingNotExistentItemDto.setStart(start);
        bookingNotExistentItemDto.setEnd(end);

        BookingDto bookingUnavailableItem = new BookingDto();
        bookingUnavailableItem.setItemId(unavailableItem.getId());
        bookingUnavailableItem.setStart(start);
        bookingUnavailableItem.setEnd(end);

        //Проверка бронирования несуществующим пользователем и попытка получения такого бронирования
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booker.getId() + 1, bookingDto));

        assertEquals(String.format("Пользователь с id %d отсутствует", booker.getId() + 1), exception.getMessage());

        final NotFoundException exception2 = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(booker.getId() + 1, createdBooking.getId() + 1));

        assertEquals(String.format("Бронирование с id %d отсутствует", createdBooking.getId() + 1), exception2.getMessage());

        //Проверка бронирования несуществующего предмета и попытка получения такого бронирования
        final NotFoundException exception3 = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booker.getId(), bookingNotExistentItemDto));

        assertEquals(String.format("Вещь с id %d отсутствует", bookingNotExistentItemDto.getItemId()), exception3.getMessage());

        final NotFoundException exception4 = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(booker.getId(), createdBooking.getId() + 1));

        assertEquals(String.format("Бронирование с id %d отсутствует", createdBooking.getId() + 1), exception4.getMessage());

        //Проверка бронирования недоступного предмета и попытка получения такого бронирования
        final InvalidValueException exception5 = assertThrows(InvalidValueException.class,
                () -> bookingService.createBooking(booker.getId(), bookingUnavailableItem));

        assertEquals("Предмет недоступен для бронирования", exception5.getMessage());

        final NotFoundException exception6 = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(booker.getId(), createdBooking.getId() + 1));

        assertEquals(String.format("Бронирование с id %d отсутствует", createdBooking.getId() + 1), exception6.getMessage());
    }
}
