package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;

public interface CommentService {
    CommentDtoResponse createComment(long authorId, long itemId, CommentDto commentDto);
}
