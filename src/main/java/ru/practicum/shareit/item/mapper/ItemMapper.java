package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseForOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class ItemMapper {
    public static Item mapToItem(User owner, ItemDto itemDto) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());

        if (itemDto.getIsAvailableForRent() != null) {
            item.setAvailableForRent(itemDto.getIsAvailableForRent());
        } else {
            item.setAvailableForRent(true);
        }

        log.debug("Вещь класса ItemDto {} преобразована в объект класса Item {}", itemDto, item);

        return item;
    }

    public static ItemDtoResponse mapToItemDtoResponse(Item item) {
        ItemDtoResponse itemDtoResponse = new ItemDtoResponse();
        UserDtoResponse userDtoResponse = UserMapper.mapToUserDtoResponse(item.getOwner());

        itemDtoResponse.setId(item.getId());
        itemDtoResponse.setOwner(userDtoResponse);
        itemDtoResponse.setName(item.getName());
        itemDtoResponse.setDescription(item.getDescription());
        itemDtoResponse.setAvailableForRent(item.isAvailableForRent());

        log.debug("Вещь класса Item {} преобразована в объект класса ItemDtoResponse {}", item, itemDtoResponse);

        return itemDtoResponse;
    }

    public static ItemDtoResponseForOwner mapToItemDtoResponseForOwner(Item item,
                                                                       LocalDateTime last,
                                                                       LocalDateTime next,
                                                                       List<CommentDtoResponse> comments) {
        ItemDtoResponseForOwner itemDtoResponse = new ItemDtoResponseForOwner();
        UserDtoResponse userDtoResponse = UserMapper.mapToUserDtoResponse(item.getOwner());

        itemDtoResponse.setId(item.getId());
        itemDtoResponse.setOwner(userDtoResponse);
        itemDtoResponse.setName(item.getName());
        itemDtoResponse.setDescription(item.getDescription());
        itemDtoResponse.setAvailableForRent(item.isAvailableForRent());
        itemDtoResponse.setLastBooking(last);
        itemDtoResponse.setNextBooking(next);
        itemDtoResponse.setComments(comments);

        log.debug("Вещь класса Item {} преобразована в объект класса ItemDtoResponseForOwner {}", item, itemDtoResponse);

        return itemDtoResponse;
    }
}
