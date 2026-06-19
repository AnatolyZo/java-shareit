package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDtoResponse;

@Data
public class ItemDtoResponse {
    private long id;
    private UserDtoResponse owner;
    private String name;
    private String description;
    @JsonProperty("available")
    private boolean isAvailableForRent;
}
