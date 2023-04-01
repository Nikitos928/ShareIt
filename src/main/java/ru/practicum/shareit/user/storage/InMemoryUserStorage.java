package ru.practicum.shareit.user.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Qualifier("InMemoryUserStorage")

public class InMemoryUserStorage implements UserStorage {


    Map<Long, User> users = new HashMap<>();

    List<String> emailList = new ArrayList<>();

    private Long id = 1L;

    @Override
    public User addUser(User user) {
        emailList.add(user.getEmail());
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

    public void updateEmail(User user, User userNew) {
        emailList.remove(user.getEmail());
        emailList.add(userNew.getEmail());
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
        emailList.remove(users.get(id).getEmail());
        users.remove(id);
    }

    public List<String> getEmailList() {

        return emailList;
    }

    public List<Long> getUserId() {

        return new ArrayList<>(users.keySet());
    }

}
