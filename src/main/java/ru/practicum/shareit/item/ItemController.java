package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@RequestHeader("X-Sharer-User-Id") long userId,
                        @PathVariable long itemId,
                        @RequestBody ItemDto itemDto) {
        return itemService.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(name = "text") String searchingSubstring) {
        return itemService.searchItems(userId, searchingSubstring);
    }
}
