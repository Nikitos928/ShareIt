package ru.practicum.shareit.Item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    @SneakyThrows
    void getAllItemsTest() {
        //User user = new User(1L, "userName", "email@mail.ru");
        UserDto user = UserDto.builder().id(1L).name("userName").email("email@mail.ru").build();
        userService.addUser(user);

        TypedQuery<User> query = em.createQuery("SELECT u from User u where u.email = :email", User.class);
        User userOut = query.setParameter("email", user.getEmail()).getSingleResult();

        ItemDto item = ItemDto.builder().id(1L).name("itemName").description("description").available(true).build();

        itemService.addItem(item, userOut.getId());

        TypedQuery<Item> query2 = em.createQuery("Select i from Item i where i.owner.id = :id", Item.class);

        Item itemOut = query2.setParameter("id", userOut.getId()).getSingleResult();

        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getName(), equalTo(item.getName()));
        assertThat(itemOut.getDescription(), equalTo(item.getDescription()));
        assertThat(itemOut.getOwner().getName(), equalTo(userOut.getName()));

        List<ItemWithBookingDto> items = itemService.getItems(userOut.getId());

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getOwner().getName(), equalTo(user.getName()));
    }
}
