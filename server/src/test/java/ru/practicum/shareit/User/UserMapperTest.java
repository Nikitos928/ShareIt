package ru.practicum.shareit.User;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapperTest {

    UserDto userDto = UserDto.builder().id(2L).name("DtoName").email("dto@email.ru").build();

    @Test
    void toUserDto() {
        User user = new User(1L, "Name", "email@email.ru");
        UserDto user1 = UserMapper.toUserDto(user);
        Assertions.assertEquals(user1, UserDto.builder().id(1L).name("Name").email("email@email.ru").build());
    }

    @Test
    void toUser() {
        UserDto userDto = UserDto.builder().id(2L).name("DtoName").email("dto@email.ru").build();
        User user = UserMapper.toUser(userDto);
        Assertions.assertEquals(user.getId(), 2L);
        Assertions.assertEquals(user.getEmail(), "dto@email.ru");
        Assertions.assertEquals(user.getName(), "DtoName");
    }
}
