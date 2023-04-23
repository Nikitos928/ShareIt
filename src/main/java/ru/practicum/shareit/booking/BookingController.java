package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundexception;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) throws NotFoundexception, BadRequestException {

        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approved(@PathVariable Long bookingId,
                               @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                               @RequestParam Boolean approved) throws NotFoundexception, BadRequestException {
        return bookingService.bookingApproving(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId,
                               @RequestHeader(value = "X-Sharer-User-Id") Long userId) throws NotFoundexception {
        return BookingMapper.toBookingDto(bookingService.getById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) throws NotFoundexception, InternalServerErrorException {

        return bookingService.getAllBookingsByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingItemsByUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) throws NotFoundexception, InternalServerErrorException {

        return bookingService.getAllBookingItemsByUser(state, userId);
    }

}
