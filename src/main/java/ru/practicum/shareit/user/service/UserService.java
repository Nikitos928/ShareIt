package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userStorage;

    public UserService(UserRepository userStorage) {
        this.userStorage = userStorage;
    }

    @Transactional
    public UserDto addUser(UserDto user) throws ValidationException {

        checkEmail(user);

        return UserMapper.toUserDto(userStorage.save(UserMapper.toUser(user)));

    }

    @Transactional
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

    public UserDto getUser(Long id) throws NotFoundException {
        User user = userStorage.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id= " + id + " не найден!"));
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getUsers() {
        List<UserDto> userDtos = new ArrayList<>();

        for (User value : userStorage.findAll()) {
            userDtos.add(UserMapper.toUserDto(value));
        }
        return userDtos;
    }

    @Transactional
    public void deleteUser(Long id) {
        userStorage.deleteById(id);
    }

    private void checkEmail(UserDto user) throws ValidationException {
        if (userStorage.existsByEmail(user.getEmail())) {
            throw new ValidationException("Такой email уже используется.");
        }
    }

}
