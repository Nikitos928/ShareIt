package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;


    @PostMapping
    public ItemRequestDto addRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                     @RequestHeader(value = "X-Sharer-User-Id") Long userId) throws NotFoundException {
        return itemRequestService.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId) throws NotFoundException {

        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getNotUserRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(required = false) Integer from,
                                                   @Positive @RequestParam(required = false) Integer size) throws NotFoundException, BadRequestException {
        return itemRequestService.getNotUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                         @RequestHeader(value = "X-Sharer-User-Id") Long userId) throws NotFoundException {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
