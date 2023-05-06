package ru.practicum.shareit.User;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor <User> userArgumentCaptor;

    User user = new User(1L, "Name", "qwe@qwe.ru");

    User user1 = new User(2L, "Name1", "qwe123@qwe.ru");
    UserDto userDto = UserMapper.toUserDto(user);

    Long userId = 1L;


    @Test
    void updateUserEmailFail() {
        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setName("NewName");
        updateUser.setEmail("NewQwe@qwe.ru");
        UserDto updateUserDto = UserMapper.toUserDto(updateUser);

        when(userRepository.getById(userId)).thenReturn(user);
        when(userRepository.existsByEmail(Mockito.any())).thenReturn(true);

        Assertions.assertThrows(ValidationException.class,
                () -> userService.updateUser(userId, updateUserDto));

    }

    @Test
    @SneakyThrows
    void updateUser() {
        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setName("NewName");
        updateUser.setEmail("NewQwe@qwe.ru");
        UserDto updateUserDto = UserMapper.toUserDto(updateUser);

        when(userRepository.getById(userId)).thenReturn(user);
        when(userRepository.existsByEmail(Mockito.any())).thenReturn(false);
        when(userRepository.save(Mockito.any())).thenReturn(updateUser);



        Assertions.assertEquals(userService.updateUser(userId, updateUserDto), updateUserDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User userCaptor = userArgumentCaptor.getValue();

        Assertions.assertEquals(userCaptor.getId(), 1L);
        Assertions.assertEquals(userCaptor.getName(), "NewName");
        Assertions.assertEquals(userCaptor.getEmail(), "NewQwe@qwe.ru");
    }

    @Test
    void getUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user1));

        Assertions.assertEquals(userService.getUsers(), Arrays.asList(UserMapper.toUserDto(user), UserMapper.toUserDto(user1)));
    }


    @Test
    void addUser_whenUserNotSave_thenBadRequestException() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(Mockito.any())).thenThrow(new DataIntegrityViolationException(""));

        Assertions.assertThrows(BadRequestException.class, () -> userService.addUser(userDto));
    }

    @Test
    void addUser_whenUserNotSave_thenValidationException() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        Assertions.assertThrows(ValidationException.class, () -> userService.addUser(userDto));
    }

    @Test
    @SneakyThrows
    void addUser_whenUserSave_thenReturnedUser() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(Mockito.any())).thenReturn(user);

        Assertions.assertEquals(userService.addUser(userDto).getId(), UserMapper.toUserDto(user).getId());
        Assertions.assertEquals(userService.addUser(userDto).getName(), UserMapper.toUserDto(user).getName());
        Assertions.assertEquals(userService.addUser(userDto).getEmail(), UserMapper.toUserDto(user).getEmail());
    }

    @Test
    @SneakyThrows
    void getUser_whenUserFound_thenReturnedUser() {
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        UserDto user1 = userService.getUser(userId);

        Assertions.assertEquals(user1, UserMapper.toUserDto(user));
    }

    @Test
    void getUserTrow_whenUserNotFound_thenNotFoundException() {
        when(userRepository.getReferenceById(userId)).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }

}
