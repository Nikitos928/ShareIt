package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    @SneakyThrows
    void getAllBookingsByUserTest() {
        UserDto userDto = UserDto.builder().id(1L).name("userName").email("email@email.ru").build();

        UserDto bookerDto = UserDto.builder().id(2L).name("user2Name").email("email2@email.ru").build();

        userService.addUser(userDto);
        userService.addUser(bookerDto);

        ItemDto itemDto = ItemDto.builder().id(1L).name("itemName").description("description").available(true).build();



        itemService.addItem(itemDto, 1L);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.owner.id = :id", Item.class);

        Item itemOut = query.setParameter("id", userDto.getId()).getSingleResult();

        Assertions.assertEquals(itemOut.getId(), 1);
        assertThat(itemOut.getOwner().getName(), equalTo(userDto.getName()));

        BookingDto booking = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                .booker(bookerDto)
                .status(Status.WAITING)
                .item(itemDto)
                .itemId(1L)
                .build();

        bookingService.addBooking(booking, userService.getUser(bookerDto.getId()).getId());

        List<BookingDto> bookingList = bookingService.getAllBookingsByUser("ALL",
                userService.getUser(bookerDto.getId()).getId(), null, null);

        Assertions.assertEquals(bookingList.size(), 1);

    }
}
