package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Builder
@Value
public class UserDto {
    Long id;
    String name;
    @Email
    String email;
}
