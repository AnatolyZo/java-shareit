package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Slf4j
public class ItemMapper {
    public static Item mapToItem(User owner, ItemDto itemDto) {
        Item item = new Item();
        item.setOwner(owner);
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
}
