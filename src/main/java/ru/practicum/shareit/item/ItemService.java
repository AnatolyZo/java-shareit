package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto editItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long userId, long itemId);

    Collection<ItemDto> getAllItems(long userId);

    Collection<ItemDto> searchItems(long userId, String searchingSubstring);
}
