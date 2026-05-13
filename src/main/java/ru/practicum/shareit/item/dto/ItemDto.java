package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class ItemDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @JsonProperty("available")
    @NotNull
    private Boolean isAvailableForRent;
    private ItemRequest request;
}
