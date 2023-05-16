package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User item) {

        return UserDto.builder().id(item.getId()).name(item.getName()).email(item.getEmail()).build();
    }

    public static User toUser(UserDto item) {
        return User.builder().email(item.getEmail()).id(item.getId()).name(item.getName()).build();

    }
}
