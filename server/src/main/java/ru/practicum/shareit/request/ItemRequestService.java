package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoResponse> getUsersRequests(long userId);

    List<ItemRequestDtoResponse> getAllRequests(long userId);

    ItemRequestDtoResponse getRequestById(long userId, long requestId);
}
