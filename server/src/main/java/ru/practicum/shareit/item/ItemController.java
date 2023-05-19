package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public ItemDto addItem(@RequestBody ItemDto item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {

        return itemService.addItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable(value = "id") Long itemId,
                              @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {

        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping
    public List<ItemWithBookingDto> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) throws NotFoundException {
        return itemService.getItems(ownerId).stream().sorted(Comparator.comparingLong(ItemWithBookingDto::getId)).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(defaultValue = "") String text,
                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size) throws BadRequestException {
        return itemService.searchItem(text, from, size);
    }

    @GetMapping("/{id}")
    public ItemWithBookingDto getItem(@PathVariable Long id,
                                      @RequestHeader(value = "X-Sharer-User-Id") Long userId) throws NotFoundException {
        return itemService.getItem(id, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) throws NotFoundException, BadRequestException {
        return itemService.createComment(commentDto, itemId, userId);
    }


}
