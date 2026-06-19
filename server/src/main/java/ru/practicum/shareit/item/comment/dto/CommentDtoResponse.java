package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDtoResponse {
    private long id;
    private String text;
    private long itemId;
    private String authorName;
    private LocalDateTime created;
}
