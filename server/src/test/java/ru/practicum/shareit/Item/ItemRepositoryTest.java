package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;


@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void search() {
        User user = new User(1L, "Name", "email@mail.ru");
        userRepository.save(user);
        Item item = new Item(1L, "Name", "description", true, user,
                null, null, null, null, null);
        Item item2 = new Item(2L, "Name2", "1234", true, user,
                null, null, null, null, null);
        itemRepository.save(item);
        itemRepository.save(item2);
        Pageable pageable = Pageable.ofSize(10);
        Page<Item> page = itemRepository.search("des", pageable);
        List<Item> items = page.toList();

        Assertions.assertNotNull(items);
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item.getId(), items.get(0).getId());
        Assertions.assertEquals(item.getName(), items.get(0).getName());
        Assertions.assertEquals(item.getDescription(), items.get(0).getDescription());
    }
}
