package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class Item {
    private long id;
    private long ownerId;
    private String name;
    private String description;
    private boolean isAvailableForRent;
    private ItemRequest request;
}
