package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        List<CommentDto> commentDtoList = new ArrayList<>();
        if (item.getComments() != null) {
            for (Comment comment : item.getComments()) {
                commentDtoList.add(toCommentDto(comment));
            }
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner() != null ? toUserDto(item.getOwner()) : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(commentDtoList)
                .build();
    }

    public static Item toItem(ItemDto item) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner() != null ? toUser(item.getOwner()) : null)
                .request(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .requestId(item.getRequestId() != null ? item.getRequestId() : null)
                .build();
    }
}
