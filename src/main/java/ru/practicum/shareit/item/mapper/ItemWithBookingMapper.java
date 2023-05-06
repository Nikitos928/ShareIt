package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingMapper.toBookingForItemDto;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestDto;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

public class ItemWithBookingMapper {

    public static ItemWithBookingDto toItemWithBookingDto(Item item) {
        List<CommentDto> commentDtoList = new ArrayList<>();
        if (item.getComments() != null) {
            for (Comment comment : item.getComments()) {
                commentDtoList.add(toCommentDto(comment));
            }
        }
        return ItemWithBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner() != null ? toUserDto(item.getOwner()) : null)
                .request(item.getRequest() != null ? toItemRequestDto(item.getRequest()) : null)
                .nextBooking(item.getNextBooking() != null ? toBookingForItemDto(item.getNextBooking()) : null)
                .lastBooking(item.getLastBooking() != null ? toBookingForItemDto(item.getLastBooking()) : null)
                .comments(commentDtoList)
                .build();
    }
}
