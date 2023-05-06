package ru.practicum.shareit.User;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    @SneakyThrows
    void add() {
        UserDto user = UserDto.builder().id(1L).email("email@mail.ru").name("userName").build();
        userService.addUser(user);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userOut = query.setParameter("email", user.getEmail()).getSingleResult();
        assertThat(userOut.getName(), equalTo(user.getName()));
        assertThat(userOut.getEmail(), equalTo(user.getEmail()));
        UserDto user2 = userService.getUser(userOut.getId());
        assertThat(userOut.getName(), equalTo(user2.getName()));
        assertThat(userOut.getEmail(), equalTo(user2.getEmail()));
    }
}
