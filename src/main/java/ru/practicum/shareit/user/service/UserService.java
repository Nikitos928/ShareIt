package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RestController
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addUser(UserDto user) throws ValidationException, BadRequestException {
        if (user.getEmail() == null) {
            throw new BadRequestException("Email не может быть пустым");
        }
        checkEmail(user);
        return UserMapper.toUserDto(userStorage.addUser(UserMapper.toUser(user)));
    }

    public UserDto updateUser(Long userId, UserDto user) throws ValidationException {
        if (userStorage.getUser(userId).getEmail().equals(user.getEmail())) {
            return UserMapper.toUserDto(userStorage.getUser(userId));
        }
        checkEmail(user);
        User updateUser = userStorage.getUser(userId);
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }

        return UserMapper.toUserDto(userStorage.updateUser(userId, updateUser));
    }

    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(userStorage.getUser(id));
    }

    public List<UserDto> getUsers() {
        List<UserDto> userDtos = new ArrayList<>();

        for (User value : userStorage.getUsers()) {
            userDtos.add(UserMapper.toUserDto(value));
        }
        return userDtos;
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    private void checkEmail(UserDto user) throws ValidationException {

        if (userStorage.findByEmail(user.getEmail())){
            throw new ValidationException();
        }
    }

}
