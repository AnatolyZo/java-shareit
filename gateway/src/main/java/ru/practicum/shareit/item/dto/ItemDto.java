package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDto {
    @NotBlank(message = "Название предмета не может быть пустым или состоять только из пробелов")
    private String name;
    @NotBlank(message = "Описание предмета не может быть пустым или состоять только из пробелов")
    private String description;
    @JsonProperty("available")
    @NotNull(message = "Параметр доступности предмета для аренды не может быть пустым")
    private Boolean isAvailableForRent;
    private Long requestId;
}
