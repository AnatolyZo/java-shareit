package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.parent.ParentStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class ItemStorageImpl extends ParentStorage<Item> implements ItemStorage {
    @Override
    public Item createItem(Item item) {
        long id = createNextId();
        item.setId(id);
        log.debug("Сформирован id {} для новой вещи", id);

        elementsMap.put(id, item);

        return item;
    }

    @Override
    public Item editItem(long itemId, Item item) {
        return elementsMap.put(itemId, item);
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(elementsMap.get(itemId));
    }

    @Override
    public Collection<Item> getAllItems(long userId) {
        return elementsMap.values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    @Override
    public Collection<Item> searchItems(String searchingSubstring) {
        return elementsMap.values()
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(searchingSubstring.toLowerCase())
                                     || item.getDescription().toLowerCase().contains(searchingSubstring.toLowerCase()))
                                     && item.isAvailableForRent()
                                     && !searchingSubstring.isEmpty())
                .toList();
    }
}
