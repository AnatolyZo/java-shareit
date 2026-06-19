package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDtoResponseForOwner {
    private long id;
    private UserDtoResponse owner;
    private String name;
    private String description;
    @JsonProperty("available")
    private boolean isAvailableForRent;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDtoResponse> comments;
}
