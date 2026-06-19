package ru.practicum.shareit.request.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class ItemRequestMapper {
    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        log.debug("Запрос предмета класса ItemRequestDto {} преобразовано в объект класса ItemRequest {}", itemRequestDto, itemRequest);
        return itemRequest;
    }

    public static ItemRequestDtoResponse mapToItemRequestDtoResponse(ItemRequest itemRequest, List<ItemDtoResponse> items) {
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setId(itemRequest.getId());
        itemRequestDtoResponse.setItems(items);
        itemRequestDtoResponse.setDescription(itemRequest.getDescription());
        itemRequestDtoResponse.setRequestorId(itemRequest.getRequestor().getId());
        itemRequestDtoResponse.setCreated(itemRequest.getCreated());

        log.debug("Запрос предмета класса ItemRequest {} преобразовано в объект класса ItemRequestDtoResponse {}", itemRequest, itemRequestDtoResponse);
        return itemRequestDtoResponse;
    }
}
