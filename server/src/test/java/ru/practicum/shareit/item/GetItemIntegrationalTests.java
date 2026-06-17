package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseForOwner;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetItemIntegrationalTests {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentService commentService;

    @Test
    void createAndGetItem() {
        UserDto user = new UserDto();
        user.setName("Name1");
        user.setEmail("name1@mail.ru");

        UserDtoResponse createdUser = userService.createUser(user);
        long createdUserId = createdUser.getId();

        ItemDto item = new ItemDto();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setIsAvailableForRent(true);

        ItemDtoResponse createdItem = itemService.createItem(createdUserId, item);
        long createdItemId = createdItem.getId();

        LocalDateTime start = LocalDateTime.of(2026, 1, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 3,12, 0,0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(createdItemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        BookingDtoResponse booking = bookingService.createBooking(createdUserId, bookingDto);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Text1");

        CommentDtoResponse comment = commentService.createComment(createdUserId, createdItemId, commentDto);

        CommentDtoResponse commentDtoResponse = new CommentDtoResponse();
        commentDtoResponse.setId(comment.getId());
        commentDtoResponse.setText(commentDto.getText());
        commentDtoResponse.setItemId(createdItemId);
        commentDtoResponse.setAuthorName(user.getName());
        commentDtoResponse.setCreated(comment.getCreated());

        ItemDtoResponseForOwner expectedItem = new ItemDtoResponseForOwner();
        expectedItem.setId(createdItemId);
        expectedItem.setOwner(createdUser);
        expectedItem.setName(item.getName());
        expectedItem.setDescription(item.getDescription());
        expectedItem.setAvailableForRent(item.getIsAvailableForRent());
        expectedItem.setComments(List.of(commentDtoResponse));

        ItemDtoResponseForOwner retrievedItem = itemService.getItemDtoById(createdUserId, createdItemId);

        assertThat(retrievedItem, equalTo(expectedItem));
    }

    @Test
    void getNotExistentItem() {
        UserDto user = new UserDto();
        user.setName("Name2");
        user.setEmail("name2@mail.ru");

        UserDtoResponse createdUser = userService.createUser(user);
        long createdUserId = createdUser.getId();

        ItemDto item = new ItemDto();
        item.setName("Item2");
        item.setDescription("Description2");
        item.setIsAvailableForRent(true);

        ItemDtoResponse createdItem = itemService.createItem(createdUserId, item);
        long createdItemId = createdItem.getId();

        LocalDateTime start = LocalDateTime.of(2026, 1, 1,12, 0,0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 3,12, 0,0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(createdItemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        BookingDtoResponse booking = bookingService.createBooking(createdUserId, bookingDto);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Text2");

        CommentDtoResponse comment = commentService.createComment(createdUserId, createdItemId, commentDto);

        //Пытаемся получить вещь со следующим id относительно последней созданной
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getItemDtoById(createdUserId, createdItemId + 1));

        assertEquals(String.format("Вещь с id %d отсутствует", createdItemId + 1), exception.getMessage());
    }

    @Test
    void getFailedToCreateItem() {
        UserDto user = new UserDto();
        user.setName("Name3");
        user.setEmail("name3@mail.ru");

        UserDtoResponse createdUser = userService.createUser(user);
        long createdUserId = createdUser.getId();

        ItemDto item = new ItemDto();
        item.setName("Item3");
        item.setDescription("Description3");
        item.setIsAvailableForRent(true);

        ItemDtoResponse createdItem = itemService.createItem(createdUserId, item);

        //Пытаемся создать вещь со следующим id относительно последней созданной
        final NotFoundException exception1 = assertThrows(NotFoundException.class,
                () -> itemService.createItem(createdUserId + 1, item));

        assertEquals(String.format("Пользователь с id %d отсутствует", createdUserId + 1), exception1.getMessage());

        //Пытаемся получить вещь со следующим id относительно последней созданной,
        //если бы предыдущий метод на создание сработал
        final NotFoundException exception2 = assertThrows(NotFoundException.class,
                () -> itemService.getItemDtoById(createdUserId, createdItem.getId() + 1));

        assertEquals(String.format("Вещь с id %d отсутствует", createdItem.getId() + 1), exception2.getMessage());
    }
}
