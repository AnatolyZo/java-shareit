package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Slf4j
public class ItemMapper {
    public static Item mapToItem(long ownerId, ItemDto itemDto) {
        Item item = new Item();
        item.setOwnerId(ownerId);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        if (itemDto.getIsAvailableForRent() != null) {
            item.setAvailableForRent(itemDto.getIsAvailableForRent());
        }

        log.debug("Вещь класса ItemDto {} преобразована в объект класса Item {}", itemDto, item);

        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setIsAvailableForRent(item.isAvailableForRent());

        log.debug("Вещь класса Item {} преобразована в объект класса ItemDto {}", item, itemDto);

        return itemDto;
    }

    public static void editItemDtoFields(Item editingItem, ItemDto changesToItem) {
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
