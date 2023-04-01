package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    public User addUser(User user);

    public User updateUser(Long userId, User user);

    public List<User> getUsers();

    public User getUser(Long id);

    public void deleteUser(Long id);

    public List<String> getEmailList();

    public List<Long> getUserId();

    public void updateEmail(User user, User userNew);

}