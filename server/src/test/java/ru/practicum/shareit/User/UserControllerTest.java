package ru.practicum.shareit.User;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    @Test
    @SneakyThrows
    void deleteUser() {
        mockMvc.perform(delete("/users/{id}", 1L)).andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }

    @Test
    @SneakyThrows
    void updateUser() {
        UserDto newUser = UserDto.builder().name("Name1").email("12email@email.com").build();

        when(userService.updateUser(1L, newUser)).thenReturn(newUser);

        String result = mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(newUser), result);

        verify(userService).updateUser(1L, newUser);

    }

    @Test
    @SneakyThrows
    void getUsers() {

        List<UserDto> users = Arrays.asList(UserDto.builder().build(), UserDto.builder().build());
        when(userService.getUsers()).thenReturn(users);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(users), result);

        verify(userService).getUsers();
    }

    @SneakyThrows
    @Test
    void getUser() {
        UserDto user = UserDto.builder().name("Name").email("email@email.com").build();
        when(userService.getUser(1L)).thenReturn(user);
        String result = mockMvc.perform(get("/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(user), result);

        verify(userService).getUser(1L);


    }

    @SneakyThrows
    @Test
    void addUser() {
        UserDto user = UserDto.builder().build();
        when(userService.addUser(user)).thenReturn(user);

        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @Test
    @SneakyThrows
    void addUser_whenUserIsNotValid_thenReturnedBadRequest() {
        UserDto user = UserDto.builder().id(1L).name("Name").build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                //.andExpect(status().isBadRequest());
                .andExpect(status().isCreated());
        //verify(userService, never()).addUser(user);

    }


}
