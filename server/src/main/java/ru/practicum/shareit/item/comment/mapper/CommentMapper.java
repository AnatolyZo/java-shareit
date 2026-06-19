package ru.practicum.shareit.item.comment.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Slf4j
public class CommentMapper {
    public static Comment mapToComment(User author, Item item, CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        log.debug("Комментарий класса CommentDto {} преобразован в объект класса Comment {}", commentDto, comment);
        return comment;
    }

    public static CommentDtoResponse mapToCommentDtoResponse(Comment comment) {
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse();

        commentDtoResponse.setId(comment.getId());
        commentDtoResponse.setText(comment.getText());
        commentDtoResponse.setItemId(comment.getItem().getId());
        commentDtoResponse.setAuthorName(comment.getAuthor().getName());
        commentDtoResponse.setCreated(comment.getCreated());

        log.debug("Комментарий класса Comment {} преобразован в объект класса CommentDtoResponse {}", comment, commentDtoResponse);
        return commentDtoResponse;
    }
}
