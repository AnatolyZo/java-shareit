package ru.practicum.shareit.item;

import org.mockito.Mock;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseForOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.validation.EntityExistsValidationService;

import java.time.LocalDateTime;
import java.util.List;

public class BaseForItemAndCommentTests {
    @Mock
    protected ItemRepository itemRepository;

    @Mock
    protected CommentRepository commentRepository;

    @Mock
    protected BookingRepository bookingRepository;

    @Mock
    protected EntityExistsValidationService entityExistsValidationService;

    protected ItemDto formItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setIsAvailableForRent(true);

        return itemDto;
    }

    protected User formUser() {
        User user = new User();
        user.setId(2L);
        user.setName("User");
        user.setEmail("user@email.ru");

        return user;
    }

    protected Item formItem() {
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

    protected ItemDtoResponse formExpectedItem() {
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

        return item;
    }

    protected Item formItemToEdit() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@email.ru");

        Item editingItem = new Item();
        editingItem.setId(1L);
        editingItem.setOwner(owner);
        editingItem.setName("OldItem");
        editingItem.setDescription("OldDescription");
        editingItem.setAvailableForRent(false);

        return editingItem;
    }

    protected ItemDtoResponseForOwner formItemForOwner(LocalDateTime lastBooking, LocalDateTime nextBooking, LocalDateTime created) {
        UserDtoResponse owner = new UserDtoResponse();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@email.ru");

        CommentDtoResponse comment = new CommentDtoResponse();
        comment.setId(1L);
        comment.setText("Text");
        comment.setItemId(1L);
        comment.setAuthorName("Author");
        comment.setCreated(created);

        ItemDtoResponseForOwner item = new ItemDtoResponseForOwner();
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailableForRent(true);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        item.setComments(List.of(comment));

        return item;
    }

    protected Comment formComment(Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Text");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        return comment;
    }

    protected User formAuthor() {
        User author = new User();
        author.setId(3L);
        author.setName("Author");
        author.setEmail("author@email.ru");

        return author;
    }

    protected CommentDto formCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Text");

        return commentDto;
    }

    protected CommentDtoResponse formExpectedComment(LocalDateTime created) {
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse();
        commentDtoResponse.setId(1L);
        commentDtoResponse.setText("Text");
        commentDtoResponse.setItemId(1L);
        commentDtoResponse.setAuthorName("Author");
        commentDtoResponse.setCreated(created);

        return commentDtoResponse;
    }
}
