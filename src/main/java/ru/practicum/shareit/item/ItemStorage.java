package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Item editItem(long itemId, Item item);

    Optional<Item> getItemById(long itemId);

    Collection<Item> getAllItems(long userId);

    Collection<Item> searchItems(String searchingSubstring);
}
