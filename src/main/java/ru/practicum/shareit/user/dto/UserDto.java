package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Builder
@Value
public class UserDto {
    Long id;
    String name;
    @Email
    String email;
}
