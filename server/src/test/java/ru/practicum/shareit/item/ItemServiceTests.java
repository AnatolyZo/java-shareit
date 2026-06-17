package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.IncorrectAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseForOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

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
public class ItemServiceTests extends BaseForItemAndCommentTests {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem() {
        long userId = 2L;
        ItemDto request = formItemDto();
        ItemDtoResponse expectedItem = formExpectedItem();

        User user = formUser();
        when(entityExistsValidationService.getUserByIdOrThrow(userId)).thenReturn(user);

        Item savedItem = formItem();
        when(itemRepository.save(any())).thenReturn(savedItem);

        ItemDtoResponse result = itemService.createItem(userId, request);

        assertThat(result, equalTo(expectedItem));
    }

    @Test
    void createItemWithNotExistentUser() {
        long userId = 999L;
        ItemDto request = formItemDto();

        when(entityExistsValidationService.getUserByIdOrThrow(userId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id %d отсутствует", userId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createItem(userId, request));

        assertEquals(String.format("Пользователь с id %d отсутствует", userId), exception.getMessage());
    }

    @Test
    void editItem() {
        long userId = 1L;
        long itemId = 1L;
        ItemDto request = formItemDto();
        ItemDtoResponse expectedItem = formExpectedItem();

        Item editingItem = formItemToEdit();
        when(entityExistsValidationService.getItemByIdOrThrow(itemId)).thenReturn(editingItem);

        Item savedItem = formItem();
        when(itemRepository.save(any())).thenReturn(savedItem);

        ItemDtoResponse result = itemService.editItem(userId, itemId, request);

        assertThat(result, equalTo(expectedItem));
    }

    @Test
    void editItemWithWrongUser() {
        long userId = 2L;
        long itemId = 1L;
        ItemDto request = formItemDto();

        Item editingItem = formItemToEdit();
        when(entityExistsValidationService.getItemByIdOrThrow(itemId)).thenReturn(editingItem);

        final IncorrectAccessException exception = assertThrows(IncorrectAccessException.class,
                () -> itemService.editItem(userId, itemId, request));

        assertEquals(String.format("У пользователя с id %d нет прав доступа для редактирования вещи с id %d", userId, itemId), exception.getMessage());
    }

    @Test
    void editNotExistentItem() {
        long userId = 1L;
        long itemId = 5L;
        ItemDto request = formItemDto();

        when(entityExistsValidationService.getItemByIdOrThrow(itemId))
                .thenThrow(new NotFoundException(String.format("Вещь с id %d отсутствует", itemId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.editItem(userId, itemId, request));

        assertEquals(String.format("Вещь с id %d отсутствует", itemId), exception.getMessage());
    }

    @Test
    void getItemDtoById() {
        long userId = 1L;
        long itemId = 1L;
        LocalDateTime created = LocalDateTime.now();
        ItemDtoResponseForOwner expectedItem = formItemForOwner(null, null, created);

        Item item = formItem();
        when(entityExistsValidationService.getItemByIdOrThrow(itemId)).thenReturn(item);

        User author = formAuthor();
        List<Comment> comments = List.of(formComment(item, author, created));
        when(commentRepository.findByItem_IdOrderByCreatedDesc(itemId)).thenReturn(comments);

        ItemDtoResponseForOwner result = itemService.getItemDtoById(userId, itemId);

        assertThat(result, equalTo(expectedItem));
    }

    @Test
    void getNotExistentItemById() {
        long userId = 1L;
        long itemId = 7L;

        when(entityExistsValidationService.getItemByIdOrThrow(itemId))
                .thenThrow(new NotFoundException(String.format("Вещь с id %d отсутствует", itemId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getItemDtoById(userId, itemId));

        assertEquals(String.format("Вещь с id %d отсутствует", itemId), exception.getMessage());
    }

    @Test
    void getAllItems() {
        long userId = 1L;
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime lastBookingDate = LocalDateTime.of(2026, 4, 1,12, 0,0);
        LocalDateTime nextBookingDate = LocalDateTime.of(2026, 7, 1,12, 0,0);
        List<ItemDtoResponseForOwner> expectedItems = List.of(formItemForOwner(lastBookingDate, nextBookingDate, created));

        Item item = formItem();
        List<Item> items = List.of(item);
        when(itemRepository.findByOwnerId(userId)).thenReturn(items);
        when(bookingRepository.findLastBookingEnd(eq(item.getId()), any())).thenReturn(lastBookingDate);
        when(bookingRepository.findNextBookingStart(eq(item.getId()), any())).thenReturn(nextBookingDate);

        User author = formAuthor();
        List<Comment> comments = List.of(formComment(item, author, created));
        when(commentRepository.findByItem_IdOrderByCreatedDesc(item.getId())).thenReturn(comments);

        List<ItemDtoResponseForOwner> result = itemService.getAllItems(userId);

        assertThat(result, equalTo(expectedItems));
    }

    @Test
    void searchItems() {
        long userId = 1L;
        String searchingSubstring = "substring";
        List<ItemDtoResponse> expectedItems = List.of(formExpectedItem());

        List<Item> items = List.of(formItem());
        when(itemRepository.findByNameOrDescription(searchingSubstring)).thenReturn(items);

        List<ItemDtoResponse> result = itemService.searchItems(userId, searchingSubstring);

        assertThat(result, equalTo(expectedItems));
    }
}
