package ru.practicum.shareit.user.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Qualifier("InMemoryUserStorage")

public class InMemoryUserStorage implements UserStorage {


    Map<Long, User> users = new HashMap<>();

    private Long id = 1L;

    @Override
    public User addUser(User user) {
        user.setId(id);
        users.put(id, user);
        id++;
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {

        users.put(user.getId(), user);
        return user;
    }


    @Override
    public List<User> getUsers() {

        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(Long id) {

        users.remove(id);
    }

    public Boolean findByEmail(String email) {

        return users.values().stream().map(User::getEmail).collect(Collectors.toList()).contains(email);
    }

    public List<Long> getUserId() {

        return new ArrayList<>(users.keySet());
    }

}
