package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UserDto {
    Long id;
    String name;

    String email;
}
