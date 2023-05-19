package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Builder
@Value
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    UserDto owner;
    Long requestId;
    List<CommentDto> comments;
}
