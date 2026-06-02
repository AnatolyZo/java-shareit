package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseForOwner;

import java.util.List;

public interface ItemService {
    ItemDtoResponse createItem(long userId, ItemDto itemDto);

    ItemDtoResponse editItem(long userId, long itemId, ItemDto itemDto);

    ItemDtoResponseForOwner getItemDtoById(long userId, long itemId);

    List<ItemDtoResponseForOwner> getAllItems(long userId);

    List<ItemDtoResponse> searchItems(long userId, String searchingSubstring);
}
