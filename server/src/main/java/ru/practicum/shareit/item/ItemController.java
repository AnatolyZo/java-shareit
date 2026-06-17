package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseForOwner;

import java.util.List;

@RestController
@RequestMapping("/internal/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDtoResponse create(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse edit(@RequestHeader("X-Sharer-User-Id") long userId,
                                @PathVariable long itemId,
                                @RequestBody ItemDto itemDto) {
        return itemService.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponseForOwner getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long itemId) {
        return itemService.getItemDtoById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoResponseForOwner> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(name = "text") String searchingSubstring) {
        return itemService.searchItems(userId, searchingSubstring);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse create(@RequestHeader("X-Sharer-User-Id") long authorId,
                                     @PathVariable long itemId,
                                     @RequestBody CommentDto commentDto) {
        return commentService.createComment(authorId, itemId, commentDto);
    }
}
