package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService requestService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    @SneakyThrows
    void getUserRequestsTest() {
        UserDto user = UserDto.builder().id(1L).name("userName").email("email@mail.ru").build();
        userService.addUser(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userOut = query.setParameter("email", user.getEmail()).getSingleResult();

        ItemRequestDto itemRequest = ItemRequestDto.builder().id(1L).description("description").build();

        requestService.addRequest(itemRequest, userOut.getId());

        TypedQuery<ItemRequest> query2 = em.createQuery("Select ir from ItemRequest ir where ir.requester.id = :requester_id",
                ItemRequest.class);
        ItemRequest itemRequestOut = query2.setParameter("requester_id", userOut.getId()).getSingleResult();

        assertThat(itemRequestOut.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestOut.getDescription(), equalTo(itemRequest.getDescription()));

        List<ItemRequestDto> itemRequests = requestService.getUserRequests(userOut.getId());

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequest.getDescription()));
    }
}
