package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Builder
@Value
public class ItemRequestDto {
    Long id;
    String description;
    UserDto requester;
    LocalDateTime created;
}
