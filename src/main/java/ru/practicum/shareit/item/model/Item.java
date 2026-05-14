package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    private long id;
    private User owner;
    private String name;
    private String description;
    private boolean isAvailableForRent;
    private ItemRequest request;
}
