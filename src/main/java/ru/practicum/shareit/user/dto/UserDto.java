package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Data
@Builder
@Value
public class UserDto {
    Long id;
    String name;
    @Email
    String email;
}
