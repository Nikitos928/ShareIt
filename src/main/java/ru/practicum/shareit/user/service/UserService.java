package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundexception;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RestController
public class UserService {
    private final UserRepository userStorage;

    public UserService(UserRepository userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addUser(UserDto user) throws ValidationException, BadRequestException {

        checkEmail(user);

        try {
            return UserMapper.toUserDto(userStorage.save(UserMapper.toUser(user)));
        } catch (Exception o) {
            throw new BadRequestException();
        }

    }

    public UserDto updateUser(Long userId, UserDto user) throws ValidationException {
        if (userStorage.getById(userId).getEmail().equals(user.getEmail())) {
            return UserMapper.toUserDto(userStorage.getById(userId));
        }
        checkEmail(user);
        User updateUser = userStorage.getById(userId);
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }

        return UserMapper.toUserDto(userStorage.save(updateUser));

    }

    public UserDto getUser(Long id) throws NotFoundexception {
        checkUserId(id);
        return UserMapper.toUserDto(userStorage.getById(id));
    }

    public List<UserDto> getUsers() {
        List<UserDto> userDtos = new ArrayList<>();

        for (User value : userStorage.findAll()) {
            userDtos.add(UserMapper.toUserDto(value));
        }
        return userDtos;
    }

    public void deleteUser(Long id) {
        userStorage.deleteById(id);
    }

    private void checkEmail(UserDto user) throws ValidationException {
        if (userStorage.findAll().stream().map(User::getEmail).collect(Collectors.toList()).contains(user.getEmail())) {
            throw new ValidationException("Такой email уже используется.");
        }
    }

    private void checkUserId(Long id) throws NotFoundexception {
        if (!userStorage.findAll().stream().map(User::getId).collect(Collectors.toList()).contains(id)) {
            throw new NotFoundexception("Пользователь с id= " + id + " не найден!");
        }
    }

}
