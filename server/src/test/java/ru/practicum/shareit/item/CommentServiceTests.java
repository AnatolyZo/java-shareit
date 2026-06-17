package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.InvalidValueException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests extends BaseForItemAndCommentTests {
    @InjectMocks
    private ItemServiceImpl commentService;

    @Test
    void createComment() {
        long authorId = 1L;
        long itemId = 1L;
        CommentDto request = formCommentDto();
        LocalDateTime created = LocalDateTime.now();
        User author = formAuthor();
        Item item = formItem();
        CommentDtoResponse expectedComment = formExpectedComment(created);

        when(entityExistsValidationService.getUserByIdOrThrow(authorId)).thenReturn(author);

        when(entityExistsValidationService.getItemByIdOrThrow(itemId)).thenReturn(item);

        Comment savedComment = formComment(item, author, created);
        when(commentRepository.save(any())).thenReturn(savedComment);
        doNothing().when(entityExistsValidationService).checkUserHasCompletedBooking(authorId, itemId);

        CommentDtoResponse result = commentService.createComment(authorId, itemId, request);

        verify(entityExistsValidationService).checkUserHasCompletedBooking(authorId, itemId);
        assertThat(result, equalTo(expectedComment));
    }

    @Test
    void createCommentWithNotExistentUser() {
        long authorId = 999L;
        long itemId = 1L;
        CommentDto request = formCommentDto();

        when(entityExistsValidationService.getUserByIdOrThrow(authorId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id %d отсутствует", authorId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.createComment(authorId, itemId, request));

        assertEquals(String.format("Пользователь с id %d отсутствует", authorId), exception.getMessage());
    }

    @Test
    void createCommentWithNotExistentItem() {
        long authorId = 1L;
        long itemId = 999L;
        CommentDto request = formCommentDto();

        User author = formAuthor();
        when(entityExistsValidationService.getUserByIdOrThrow(authorId)).thenReturn(author);
        when(entityExistsValidationService.getItemByIdOrThrow(itemId))
                .thenThrow(new NotFoundException(String.format("Вещь с id %d отсутствует", itemId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.createComment(authorId, itemId, request));

        assertEquals(String.format("Вещь с id %d отсутствует", itemId), exception.getMessage());
    }

    @Test
    void createCommentWithUncompletedBooking() {
        long authorId = 1L;
        long itemId = 1L;
        CommentDto request = formCommentDto();

        User author = formAuthor();
        when(entityExistsValidationService.getUserByIdOrThrow(authorId)).thenReturn(author);

        Item item = formItem();
        when(entityExistsValidationService.getItemByIdOrThrow(itemId)).thenReturn(item);

        doThrow(new InvalidValueException(
                String.format("Пользователь с id %d не имеет завершенных бронирований предмета с id %d, комментирование запрещено", authorId, itemId)))
                .when(entityExistsValidationService).checkUserHasCompletedBooking(eq(authorId), eq(itemId));

        final InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> commentService.createComment(authorId, itemId, request));

        assertEquals(String.format("Пользователь с id %d не имеет завершенных бронирований предмета с id %d, комментирование запрещено", authorId, itemId), exception.getMessage());
    }
}
