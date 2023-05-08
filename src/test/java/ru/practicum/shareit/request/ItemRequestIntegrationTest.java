package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestIntegrationTest {

    private final ItemRequestService requestService;
    private final UserService userService;

    @Test
    @SneakyThrows
    void getUserRequestsTest() {
        UserDto user = UserDto.builder().id(1L).name("userName").email("email@mail.ru").build();
        Long userId = userService.addUser(user).getId();

        UserDto userOut = userService.getUser(userId);
        ItemRequestDto itemRequest = ItemRequestDto.builder().id(1L).description("description").build();

        Long requestId = requestService.addRequest(itemRequest, userOut.getId()).getId();

        ItemRequestDto itemRequestOut = requestService.getRequestById(requestId, userId);

        assertThat(itemRequestOut.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestOut.getDescription(), equalTo(itemRequest.getDescription()));

        List<ItemRequestDto> itemRequests = requestService.getUserRequests(userOut.getId());

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequest.getDescription()));
    }
}
