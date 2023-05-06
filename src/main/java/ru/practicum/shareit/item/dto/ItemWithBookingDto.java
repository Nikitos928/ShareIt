package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Builder
@Value
public class ItemWithBookingDto {
    Long id;
    String name;
    String description;
    Boolean available;
    UserDto owner;
    ItemRequestDto request;
    BookingForItemDto lastBooking;
    BookingForItemDto nextBooking;
    List<CommentDto> comments;
}
