package ru.practicum.shareit.Item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    @Test
    @SneakyThrows
    void getAllItemsTest() {

        UserDto user = UserDto.builder().id(1L).name("userName").email("email1@mail.ru").build();
        Long userId = userService.addUser(user).getId();

        UserDto userOut = userService.getUser(userId);

        ItemDto item = ItemDto.builder().id(1L).name("itemName").description("description").available(true).build();

        itemService.addItem(item, userOut.getId());

        ItemWithBookingDto itemOut = itemService.getItem(1L, 1L);

        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getName(), equalTo(item.getName()));
        assertThat(itemOut.getDescription(), equalTo(item.getDescription()));
        assertThat(itemOut.getOwner().getName(), equalTo(userOut.getName()));

        List<ItemWithBookingDto> items = itemService.getItems(userOut.getId());

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getOwner().getName(), equalTo(user.getName()));
    }
}
