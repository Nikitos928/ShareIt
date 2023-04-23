package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundexception;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.CommentMapper.toComment;
import static ru.practicum.shareit.item.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.ItemWithBookingMapper.toItemWithBookingDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@Valid @RequestBody ItemDto item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundexception, BadRequestException {

        return itemService.addItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable(value = "id") Long itemId,
                              @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundexception {

        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping
    public List<ItemWithBookingDto> readAll(@RequestHeader("X-Sharer-User-Id") Long ownerId) throws NotFoundexception {
        return itemService.getItems(ownerId).stream().sorted(Comparator.comparingLong(ItemWithBookingDto::getId)).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @GetMapping("/{id}")
    public ItemWithBookingDto getItem(@PathVariable Long id,
                                      @RequestHeader(value = "X-Sharer-User-Id") Long userId) throws NotFoundexception {
        return toItemWithBookingDto(itemService.getItem(id, userId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) throws NotFoundexception, BadRequestException {
        return toCommentDto(itemService.createComment(toComment(commentDto), itemId, userId));
    }


}
