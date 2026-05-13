package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        log.trace("Пользователь с id {} инициировал создание вещи {}", userId, itemDto);

        //Получение пользователя для проверки начличия такого пользователя в хранилище
        userService.getUserById(userId);
        Item convertedRequestItem = ItemMapper.mapToItem(userId, itemDto);
        Item createdItem = itemStorage.createItem(convertedRequestItem);
        log.debug("Создана вещь {} и добавлена в хранилище", createdItem);
        return ItemMapper.mapToItemDto(createdItem);
    }

    @Override
    public ItemDto editItem(long userId, long itemId, ItemDto changesToItem) {
        log.trace("Пользователь с id {} инициировал редактирование вещи с id {}", userId, itemId);

        //Проверка наличия вещи в хранилище и права доступа пользователя к этой вещи
        Item editingItem = getItemByIdOrThrow(itemId);
        checkUsersAccessToEditItem(userId, editingItem);

        ItemMapper.editItemDtoFields(editingItem, changesToItem);
        Item editedItem = itemStorage.editItem(itemId, editingItem);
        log.debug("Отредактированы данные вещи {}, стало - {}", editingItem, editedItem);
        return ItemMapper.mapToItemDto(editedItem);
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        log.trace("Пользователь с id {} инициировал получение вещи с id {}", userId, itemId);
        Item item = getItemByIdOrThrow(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItems(long userId) {
        log.trace("Пользователь с id {} инициировал получение всех вещей", userId);

        return itemStorage.getAllItems(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(long userId, String searchingSubstring) {
        log.trace("Пользователь с id {} инициировал поиск вещий по подстроке \"{}\"", userId, searchingSubstring);

        return itemStorage.searchItems(searchingSubstring)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    private void checkUsersAccessToEditItem(long userId, Item editingItem) {
        if (editingItem.getOwnerId() != userId) {
            log.warn("Пользователю с id {} отказано в доступе к редактированию вещи {}", userId, editingItem);
            throw new IncorrectAccessException(String.format("У пользователя с id %d нет прав доступа для редактирования вещи с id %d", userId, editingItem.getId()));
        }
    }

    private Item getItemByIdOrThrow(long itemId) {
        return itemStorage.getItemById(itemId)
                .orElseThrow(() -> {
                    log.info("Вещь с id {} отсутствует", itemId);
                    return new NotFoundException(String.format("Вещь с id %d отсутствует", itemId));
                });
    }
}
