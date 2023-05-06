package ru.practicum.shareit.User;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void existsByEmailTest() {
        userRepository.save(new User(1L, "Name", "email@new.ru"));
        userRepository.save(new User(2L, "Name", "1email@new.ru"));

        boolean actualUser = userRepository.existsByEmail("email@new.ru");
        boolean notActualUser = userRepository.existsByEmail("111email@new.ru");

        Assertions.assertTrue(actualUser);
        Assertions.assertFalse(notActualUser);
    }

    @AfterEach
    void deleteUser() {
        userRepository.deleteAll();
    }


}
