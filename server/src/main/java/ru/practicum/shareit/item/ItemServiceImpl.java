package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.IncorrectAccessException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseForOwner;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.EntityExistsValidationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService, CommentService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final EntityExistsValidationService entityExistsValidationService;

    @Override
    @Transactional
    public ItemDtoResponse createItem(long userId, ItemDto itemDto) {
        log.trace("Пользователь с id {} инициировал создание вещи {}", userId, itemDto);

        //Получение пользователя для проверки начличия такого пользователя в хранилище
        User owner = entityExistsValidationService.getUserByIdOrThrow(userId);

        ItemRequest itemRequest;
        if (itemDto.getRequestId() != null) {
            itemRequest = entityExistsValidationService.getItemRequestByIdOrThrow(itemDto.getRequestId());
        } else {
            itemRequest = null;
        }

        Item convertedRequestItem = ItemMapper.mapToItem(owner, itemDto, itemRequest);
        Item createdItem = itemRepository.save(convertedRequestItem);
        log.debug("Создана вещь {} и добавлена в хранилище", createdItem);
        return ItemMapper.mapToItemDtoResponse(createdItem);
    }

    @Override
    @Transactional
    public ItemDtoResponse editItem(long userId, long itemId, ItemDto changesToItem) {
        log.trace("Пользователь с id {} инициировал редактирование вещи с id {}", userId, itemId);

        //Проверка наличия вещи в хранилище и права доступа пользователя к этой вещи
        Item editingItem = entityExistsValidationService.getItemByIdOrThrow(itemId);
        checkUsersAccessToEditItem(userId, editingItem);

        editItemFields(editingItem, changesToItem);
        Item editedItem = itemRepository.save(editingItem);
        log.debug("Отредактированы данные вещи {}, стало - {}", editingItem, editedItem);
        return ItemMapper.mapToItemDtoResponse(editedItem);
    }

    @Override
    public ItemDtoResponseForOwner getItemDtoById(long userId, long itemId) {
        log.trace("Пользователь с id {} инициировал получение вещи с id {}", userId, itemId);
        Item item = entityExistsValidationService.getItemByIdOrThrow(itemId);
        List<Comment> comments = commentRepository.findByItem_IdOrderByCreatedDesc(itemId);
        List<CommentDtoResponse> convertedComments = comments.stream()
                .map(CommentMapper::mapToCommentDtoResponse)
                .toList();
        return ItemMapper.mapToItemDtoResponseForOwner(item, null, null, convertedComments);
    }

    @Override
    public List<ItemDtoResponseForOwner> getAllItems(long userId) {
        log.trace("Пользователь с id {} инициировал получение всех вещей", userId);
        LocalDateTime now = LocalDateTime.now();
        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(item -> {
                    LocalDateTime lastBookingDate = bookingRepository.findLastBookingEnd(item.getId(), now);
                    LocalDateTime nextBookingDate = bookingRepository.findNextBookingStart(item.getId(), now);
                    List<Comment> comments = commentRepository.findByItem_IdOrderByCreatedDesc(item.getId());
                    List<CommentDtoResponse> convertedComments = comments.stream()
                            .map(CommentMapper::mapToCommentDtoResponse)
                            .toList();
                    return ItemMapper.mapToItemDtoResponseForOwner(item, lastBookingDate, nextBookingDate, convertedComments);
                })
                .toList();
    }

    @Override
    public List<ItemDtoResponse> searchItems(long userId, String searchingSubstring) {
        log.trace("Пользователь с id {} инициировал поиск вещий по подстроке \"{}\"", userId, searchingSubstring);

        return itemRepository.findByNameOrDescription(searchingSubstring)
                .stream()
                .map(ItemMapper::mapToItemDtoResponse)
                .toList();
    }

    @Override
    @Transactional
    public CommentDtoResponse createComment(long authorId, long itemId, CommentDto commentDto) {
        User author = entityExistsValidationService.getUserByIdOrThrow(authorId);
        Item item = entityExistsValidationService.getItemByIdOrThrow(itemId);
        entityExistsValidationService.checkUserHasCompletedBooking(authorId, itemId);

        Comment comment = CommentMapper.mapToComment(author, item, commentDto);
        Comment createdComment = commentRepository.save(comment);
        return CommentMapper.mapToCommentDtoResponse(createdComment);
    }

    private void checkUsersAccessToEditItem(long userId, Item editingItem) {
        if (editingItem.getOwner().getId() != userId) {
            log.warn("Пользователю с id {} отказано в доступе к редактированию вещи {}", userId, editingItem);
            throw new IncorrectAccessException(String.format("У пользователя с id %d нет прав доступа для редактирования вещи с id %d", userId, editingItem.getId()));
        }
    }

    private void editItemFields(Item editingItem, ItemDto changesToItem) {
        if (changesToItem.getName() != null) {
            editingItem.setName(changesToItem.getName());
            log.debug("У вещи {} отредактировано поле name на {}", editingItem, changesToItem.getName());
        }

        if (changesToItem.getDescription() != null) {
            editingItem.setDescription(changesToItem.getDescription());
            log.debug("У вещи {} отредактировано поле description на {}", editingItem, changesToItem.getDescription());
        }

        if (changesToItem.getIsAvailableForRent() != null) {
            editingItem.setAvailableForRent(changesToItem.getIsAvailableForRent());
            log.debug("У вещи {} отредактировано поле isAvailableForRent на {}", editingItem, changesToItem.getIsAvailableForRent());
        }
    }
}
