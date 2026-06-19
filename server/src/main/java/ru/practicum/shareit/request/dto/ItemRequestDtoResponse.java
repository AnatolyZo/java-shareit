package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDtoResponse {
    private long id;
    private List<ItemDtoResponse> items;
    private String description;
    private long requestorId;
    private LocalDateTime created;
}
