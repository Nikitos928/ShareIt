package ru.practicum.shareit.Item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemWithBookingMapper.toItemWithBookingDto;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemStorage;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userStorage;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @InjectMocks
    ItemService itemService;

    Item item;

    ItemDto itemDto;

    @BeforeEach
    void add() {
        User user = User.builder().id(11L).email("email@email.ru").name("Name").build();
        item = new Item(1L, "Name", "Desc", true, user, new ItemRequest(),
                null, null, new ArrayList<Comment>(), 2L);
        itemDto = ItemDto.builder().available(true).build();
    }


    @Test
    @SneakyThrows
    void createComment() {

        when(userStorage.findById(Mockito.any())).thenReturn(Optional.of(new User()));
        when(itemStorage.findById(Mockito.any())).thenReturn(Optional.of(new Item()));
        when(bookingRepository.findByBookerAndItemAndEndBefore(Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(Arrays.asList(new Booking()));
        CommentDto commentDto = CommentDto.builder().id(22L).text("1234").build();
        Comment comment = Comment.builder().author(User.builder().name("1234").build()).build();
        when(commentRepository.save(Mockito.any())).thenReturn(comment);

        Assertions.assertEquals(itemService.createComment(commentDto, 111L, 11L), toCommentDto(comment));
    }

    @Test
    @SneakyThrows
    void createComment_whenBookingIsEmpty_thenBadRequestException() {

        when(userStorage.findById(Mockito.any())).thenReturn(Optional.of(new User()));
        when(itemStorage.findById(Mockito.any())).thenReturn(Optional.of(new Item()));
        when(bookingRepository.findByBookerAndItemAndEndBefore(Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(new ArrayList<>());


        Assertions.assertThrows(BadRequestException.class,
                () -> itemService.createComment(CommentDto.builder().text("1234").build(), 111L, 11L));

    }


    @Test
    @SneakyThrows
    void createComment_whenCommentDtoGetTextIsBlank_thenBadRequestException() {

        when(userStorage.findById(Mockito.any())).thenReturn(Optional.of(new User()));
        when(itemStorage.findById(Mockito.any())).thenReturn(Optional.of(new Item()));
        when(bookingRepository.findByBookerAndItemAndEndBefore(Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(new ArrayList<>());
        Assertions.assertThrows(BadRequestException.class,
                () -> itemService.createComment(CommentDto.builder().text("").build(), 111L, 11L));

    }

    @Test
    @SneakyThrows
    void createComment_whenUserNotFound_thenNotFoundException() {

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createComment(CommentDto.builder().build(), 111L, 11L));
    }


    @Test
    @SneakyThrows
    void createComment_whenItemNotFound_thenNotFoundException() {

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createComment(CommentDto.builder().build(), 111L, 11L));
    }


    @Test
    @SneakyThrows
    void getItems() {
        when(userStorage.existsById(Mockito.any())).thenReturn(true);
        when(bookingRepository.findByItemOwnerOrderByStartDesc(Mockito.any())).thenReturn(new ArrayList<>());
        when(itemStorage.getItemsByOwnerId(Mockito.any())).thenReturn(new ArrayList<>());
        Assertions.assertEquals(itemService.getItems(1L), new ArrayList<>());
    }


    @Test
    void getItems_whenUserNotFound_thenNotFoundException() {
        when(userStorage.existsById(Mockito.any())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItems(111L));
    }

    @Test
    @SneakyThrows
    void getItem() {
        when(itemStorage.findById(Mockito.any())).thenReturn(Optional.ofNullable(item));
        when(commentRepository.findCommentsByItemOrderByCreatedDesc(Mockito.any())).thenReturn(new ArrayList<>());

        Assertions.assertEquals(itemService.getItem(1L, 1L), toItemWithBookingDto(item));
    }

    @Test
    void getItem_whenItemNotFound_thenNotFoundException() {

        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 1L));
        verify(itemStorage, never()).getById(Mockito.any());
        verify(commentRepository, never()).findCommentsByItemOrderByCreatedDesc(Mockito.any());

    }

    @Test
    @SneakyThrows
    void updateItem() {
        item.setComments(Arrays.asList(Comment.builder()
                .item(item)
                .author(new User()).build(), Comment.builder()
                .item(item)
                .author(new User())
                .build()));

        Item newItem = Item.builder().name("NewName").description("NewDescription").available(false).build();
        when(itemStorage.getById(Mockito.any())).thenReturn(item);
        when(userStorage.existsById(Mockito.any())).thenReturn(true);
        when(itemStorage.save(Mockito.any())).thenReturn(item);


        Assertions.assertEquals(itemService.updateItem(1L, ItemMapper.toItemDto(newItem), 11L),
                ItemMapper.toItemDto(item));

        verify(itemStorage).save(itemArgumentCaptor.capture());
        Item itemCaptor = itemArgumentCaptor.getValue();

        Assertions.assertEquals(itemCaptor.getId(), 1L);
        Assertions.assertEquals(itemCaptor.getName(), "NewName");
        Assertions.assertEquals(itemCaptor.getDescription(), "NewDescription");
        Assertions.assertEquals(itemCaptor.getAvailable(), false);

    }

    @Test
    void updateItem_whenUserNotFound_thenNotFoundException() {
        when(itemStorage.getById(Mockito.any())).thenReturn(item);
        when(userStorage.existsById(Mockito.any())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, 11L));
        verify(itemStorage, never()).save(Mockito.any());
    }

    @Test
    void updateItem_whenOwnerNotFound_thenNotFoundException() {
        when(itemStorage.getById(Mockito.any())).thenReturn(item);

        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, 1L));

        verify(itemStorage, never()).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addItem() {
        when(userStorage.findById(Mockito.any())).thenReturn(Optional.of(new User()));
        when(itemStorage.save(Mockito.any())).thenReturn(item);

        Assertions.assertEquals(itemService.addItem(itemDto, 1L), ItemMapper.toItemDto(item));

        verify(itemStorage).save(Mockito.any());

    }

    @Test
    void addItem_whenUserNotFound_thenBadRequestException() {


        Assertions.assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, 1L));

        verify(itemStorage, never()).save(Mockito.any());

    }


    @Test
    void addItem_whenUserAvailableNull_thenBadRequestException() {
        ItemDto itemDto = ItemDto.builder().build();

        Assertions.assertThrows(BadRequestException.class, () -> itemService.addItem(itemDto, 1L));

        verify(itemStorage, never()).save(Mockito.any());
    }

}
