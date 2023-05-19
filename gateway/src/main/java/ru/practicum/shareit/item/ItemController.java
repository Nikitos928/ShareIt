package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader(value = "X-Sharer-User-Id") long userId) throws BadRequestException {
        log.info("Create item={} with userId={}", itemDto, userId);
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException();
        }
        return itemClient.create(itemDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Get item with itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId) {
        log.info("Update item {} itemId={}, userId={}", itemDto, itemId, userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam(name = "text", defaultValue = "") String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search item with text={}", text);
        return itemClient.searchItem(text, from, size);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable long itemId) {
        log.info("Delete item with itemId={}", itemId);
        return itemClient.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                @PathVariable long itemId) throws BadRequestException {
        log.info("Create comment {} to item={}, user={}", commentDto, itemId, userId);
        if (commentDto.getText().isBlank()) {
            throw new BadRequestException("Коментарий не может быть пустым");
        }
        return itemClient.createComment(commentDto, itemId, userId);
    }
}
