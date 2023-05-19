package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                             @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Create itemRequest={} with userId={}", itemRequestDto, userId);
        return itemRequestClient.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequest(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Get itemRequests with userId={}", userId);
        return itemRequestClient.getUserRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getNotUserRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get itemRequests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getNotUserRequests(userId, from, size);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long itemRequestId,
                                                 @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Get itemRequest with itemRequestId={}, userId={}", itemRequestId, userId);
        return itemRequestClient.getRequestById(itemRequestId, userId);
    }
}
