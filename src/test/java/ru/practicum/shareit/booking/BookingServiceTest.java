package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PageableCreater pageableCreater;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptorArgumentCaptor;

    @InjectMocks
    BookingService bookingService;


    List<Booking> bookingList = Arrays.asList(Booking.builder().id(1L)
                    .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                    .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                    .booker(User.builder().build())
                    .status(Status.WAITING)
                    .item(Item.builder().id(11L).build())
                    .build(),

            Booking.builder().id(2L)
                    .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                    .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                    .booker(User.builder().build())
                    .status(Status.WAITING)
                    .item(Item.builder().id(11L).build())
                    .build());


    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenStateALL() {

        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(userRepository.getById(Mockito.any())).thenReturn(new User());
        when(bookingRepository.findByBookerOrderByStartDesc(
                Mockito.any(),
                Mockito.any()))
                .thenReturn(bookingList);

        Assertions.assertEquals(bookingService.getAllBookingsByUser("ALL", 111L, 1, 2),
                bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList()));
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenStateREJECTED() {

        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(userRepository.getById(Mockito.any())).thenReturn(new User());
        when(bookingRepository.findByBookerAndStatusOrderByStartDesc(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(bookingList);

        Assertions.assertEquals(bookingService.getAllBookingsByUser("REJECTED", 111L, 1, 2),
                bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList()));
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenStateWAITING() {

        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(userRepository.getById(Mockito.any())).thenReturn(new User());
        when(bookingRepository.findByBookerAndStatusOrderByStartDesc(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(bookingList);

        Assertions.assertEquals(bookingService.getAllBookingsByUser("WAITING", 111L, 1, 2),
                bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList()));
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenStateFUTURE() {

        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(userRepository.getById(Mockito.any())).thenReturn(new User());
        when(bookingRepository.findByBookerAndStartAfterOrderByStartDesc(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(bookingList);

        Assertions.assertEquals(bookingService.getAllBookingsByUser("FUTURE", 111L, 1, 2),
                bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList()));
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenStatePAST() {

        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(userRepository.getById(Mockito.any())).thenReturn(new User());
        when(bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(bookingList);

        Assertions.assertEquals(bookingService.getAllBookingsByUser("PAST", 111L, 1, 2),
                bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList()));
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenStateCURRENT() {

        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(userRepository.getById(Mockito.any())).thenReturn(new User());
        when(bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(bookingList);

        Assertions.assertEquals(bookingService.getAllBookingsByUser("CURRENT", 111L, 1, 2),
                bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList()));

    }


    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenStateNotFound_thenInvalidStateException() {

        when(userRepository.existsById(Mockito.any())).thenReturn(true);

        try {
            bookingService.getAllBookingsByUser("NotState", 111L, 1, 1);
        } catch (InvalidStateException o) {
            Assertions.assertEquals(o.getMessage(), "Unknown state: NotState");
        }

        Assertions.assertThrows(InvalidStateException.class, () -> bookingService.getAllBookingsByUser("NotState", 111L, 1, 1));
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenUserNotFound_thenNotFoundException() {

        when(userRepository.existsById(Mockito.any())).thenReturn(false);

        try {
            bookingService.getAllBookingsByUser("", 111L, 1, 1);
        } catch (NotFoundException o) {
            Assertions.assertEquals(o.getMessage(), "Пользователь с id= 111 не найден!");
        }

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByUser("", 111L, 1, 1));
    }


    @Test
    @SneakyThrows
    void bookingApproving_whenStatusREJECTED() {
        Booking booking = Booking.builder().id(1L)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                .booker(User.builder().build())
                .status(Status.WAITING)
                .item(Item.builder().id(11L).build())
                .build();

        User user = User.builder().id(1L).build();

        when(userRepository.getById(Mockito.any())).thenReturn(user);
        when(bookingRepository.getById(Mockito.any())).thenReturn(Booking.builder()
                .status(Status.WAITING)
                .item(Item.builder().id(1L).owner(user).build())
                .build());
        when(itemRepository.getById(Mockito.any())).thenReturn(Item.builder().owner(user).build());

        when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        Assertions.assertEquals(bookingService.bookingApproving(1L, false, 1L), BookingMapper.toBookingDto(booking));

        verify(bookingRepository).save(Mockito.any());

        verify(bookingRepository).save(bookingArgumentCaptorArgumentCaptor.capture());

        Booking itemCaptor = bookingArgumentCaptorArgumentCaptor.getValue();

        Assertions.assertEquals(itemCaptor.getStatus(), Status.REJECTED);

    }

    @Test
    @SneakyThrows
    void getById() {
        User user = new User();
        Booking booking = Booking.builder()
                .booker(user)
                .item(Item.builder().owner(User.builder().build()).build())
                .build();
        when(userRepository.getById(Mockito.any())).thenReturn(user);
        when(bookingRepository.existsById(Mockito.any())).thenReturn(true);
        when(bookingRepository.getById(Mockito.any())).thenReturn(booking);

        Assertions.assertEquals(bookingService.getById(1L, 1L), BookingMapper.toBookingDto(booking));

    }

    @Test
    @SneakyThrows
    void getById_whenBookerNotUser_thenNotFoundException() {
        when(userRepository.getById(Mockito.any())).thenReturn(User.builder().build());
        when(bookingRepository.existsById(Mockito.any())).thenReturn(true);
        when(bookingRepository.getById(Mockito.any())).thenReturn(Booking.builder()
                .booker(User.builder().build())
                .item(Item.builder().owner(User.builder().build()).build())
                .build());
        try {
            bookingService.getById(1L, 1L);
        } catch (NotFoundException o) {
            Assertions.assertEquals(o.getMessage(), "Только владелец или арендатор может получить информацию о бронировании");
        }

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    }

    @Test
    @SneakyThrows
    void getById_whenBookingNotFound_thenNotFoundException() {
        when(userRepository.getById(Mockito.any())).thenReturn(User.builder().build());
        when(bookingRepository.existsById(Mockito.any())).thenReturn(false);
        try {
            bookingService.getById(1L, 1L);
        } catch (NotFoundException o) {
            Assertions.assertEquals(o.getMessage(), "Бронирование с id=1 не найдено");
        }

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    }

    @Test
    @SneakyThrows
    void bookingApproving_whenStatusAPPROVED() {
        Booking booking = Booking.builder().id(1L)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                .booker(User.builder().build())
                .status(Status.WAITING)
                .item(Item.builder().id(11L).build())
                .build();

        User user = User.builder().id(1L).build();

        when(userRepository.getById(Mockito.any())).thenReturn(user);
        when(bookingRepository.getById(Mockito.any())).thenReturn(Booking.builder()
                .status(Status.WAITING)
                .item(Item.builder().id(1L).owner(user).build())
                .build());
        when(itemRepository.getById(Mockito.any())).thenReturn(Item.builder().owner(user).build());

        when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        Assertions.assertEquals(bookingService.bookingApproving(1L, true, 1L), BookingMapper.toBookingDto(booking));

        verify(bookingRepository).save(Mockito.any());

        verify(bookingRepository).save(bookingArgumentCaptorArgumentCaptor.capture());

        Booking itemCaptor = bookingArgumentCaptorArgumentCaptor.getValue();

        Assertions.assertEquals(itemCaptor.getStatus(), Status.APPROVED);

    }

    @Test
    @SneakyThrows
    void bookingApproving_whenBookerNotTheOwnerOfTheItem_thenNotFoundException() {
        when(userRepository.getById(Mockito.any())).thenReturn(User.builder().build());
        when(bookingRepository.getById(Mockito.any())).thenReturn(Booking.builder()
                .status(Status.WAITING)
                .item(Item.builder().id(11L).build())
                .build());
        when(itemRepository.getById(Mockito.any())).thenReturn(Item.builder().build());

        try {
            bookingService.bookingApproving(1L, true, 1L);
        } catch (NotFoundException o) {
            Assertions.assertEquals(o.getMessage(), "Подтведить может только владелец");
        }

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.bookingApproving(1L, true, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void bookingApproving_whenBookingStatusAPPROVED_thenBadRequestException() {
        when(userRepository.getById(Mockito.any())).thenReturn(User.builder().build());
        when(bookingRepository.getById(Mockito.any())).thenReturn(Booking.builder()
                .status(Status.APPROVED)
                .item(Item.builder().id(11L).build())
                .build());
        when(itemRepository.getById(Mockito.any())).thenReturn(Item.builder().build());

        try {
            bookingService.bookingApproving(1L, true, 1L);
        } catch (BadRequestException o) {
            Assertions.assertEquals(o.getMessage(), "Бронирование уже одобрино");
        }

        Assertions.assertThrows(BadRequestException.class, () -> bookingService.bookingApproving(1L, true, 1L));

        verify(bookingRepository, never()).save(Mockito.any());

    }


    @Test
    @SneakyThrows
    void addBooking() {
        Booking booking = Booking.builder().id(1L)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                .booker(User.builder().build())
                .status(Status.WAITING)
                .item(Item.builder().id(12L).build())
                .build();
        User user = User.builder().id(22L).build();

        when(itemRepository.getReferenceById(Mockito.any())).thenReturn(Item.builder().available(true).owner(user).build());
        when(userRepository.getReferenceById(Mockito.any())).thenReturn(user);
        when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .itemId(22L)
                .build();

        Assertions.assertEquals(bookingService.addBooking(bookingDto, 1L), BookingMapper.toBookingDto(booking));

        verify(bookingRepository).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addBooking_whenOwnerBookingItem_thenNotFoundException() {
        User user = User.builder().id(22L).build();

        when(itemRepository.getReferenceById(Mockito.any())).thenReturn(Item.builder().available(true).owner(user).build());
        when(userRepository.getReferenceById(Mockito.any())).thenReturn(user);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .itemId(22L)
                .build();

        try {
            bookingService.addBooking(bookingDto, 22L);
        } catch (NotFoundException o) {
            Assertions.assertEquals(o.getMessage(), "Владелец вещинне может забронировать предмет");
        }

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, 22L));

        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addBooking_whenTimeIsNotCorrectEndInThePast_thenBadRequestException() {

        when(itemRepository.getReferenceById(Mockito.any())).thenReturn(Item.builder().available(true).build());
        when(userRepository.getReferenceById(Mockito.any())).thenReturn(new User());

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(1111, 11, 11, 11, 11))
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .itemId(22L)
                .build();

        try {
            bookingService.addBooking(bookingDto, 1L);
        } catch (BadRequestException o) {
            Assertions.assertEquals(o.getMessage(), "Некорректное время");
        }

        Assertions.assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addBooking_whenTimeIsNotCorrectStartInThePast_thenBadRequestException() {

        when(itemRepository.getReferenceById(Mockito.any())).thenReturn(Item.builder().available(true).build());
        when(userRepository.getReferenceById(Mockito.any())).thenReturn(new User());

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(1999, 11, 11, 11, 11))
                .end(LocalDateTime.of(2023, 11, 11, 11, 11))
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .itemId(22L)
                .build();

        try {
            bookingService.addBooking(bookingDto, 1L);
        } catch (BadRequestException o) {
            Assertions.assertEquals(o.getMessage(), "Некорректное время");
        }

        Assertions.assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addBooking_whenTimeIsNotCorrectEndAfterStart_thenBadRequestException() {

        when(itemRepository.getReferenceById(Mockito.any())).thenReturn(Item.builder().available(true).build());
        when(userRepository.getReferenceById(Mockito.any())).thenReturn(new User());

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 11, 11, 11, 11))
                .end(LocalDateTime.of(2023, 11, 11, 11, 11))
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .itemId(22L)
                .build();

        try {
            bookingService.addBooking(bookingDto, 1L);
        } catch (BadRequestException o) {
            Assertions.assertEquals(o.getMessage(), "Некорректное время");
        }

        Assertions.assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addBooking_whenAvailableFalse_thenBadRequestException() {

        when(itemRepository.getReferenceById(Mockito.any())).thenReturn(Item.builder().available(false).build());
        when(userRepository.getReferenceById(Mockito.any())).thenReturn(new User());

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 11, 11, 11, 11))
                .end(LocalDateTime.of(2023, 11, 11, 11, 11))
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .itemId(22L)
                .build();

        try {
            bookingService.addBooking(bookingDto, 1L);
        } catch (BadRequestException o) {
            Assertions.assertEquals(o.getMessage(), "Предмет нельзя забронировать");
        }

        Assertions.assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addBooking_whenUserNull_thenNotFoundException() {

        when(itemRepository.getReferenceById(Mockito.any())).thenReturn(new Item());
        when(userRepository.getReferenceById(Mockito.any())).thenReturn(null);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 11, 11, 11, 11))
                .end(LocalDateTime.of(2023, 11, 11, 11, 11))
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .itemId(22L)
                .build();

        try {
            bookingService.addBooking(bookingDto, 1L);
        } catch (NotFoundException o) {
            Assertions.assertEquals(o.getMessage(), "Пользователь с id=22 не найден");
        }

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }


    @Test
    @SneakyThrows
    void addBooking_whenItemNull_thenNotFoundException() {

        when(itemRepository.getReferenceById(Mockito.any())).thenReturn(null);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 11, 11, 11, 11))
                .end(LocalDateTime.of(2023, 11, 11, 11, 11))
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .itemId(22L)
                .build();

        try {
            bookingService.addBooking(bookingDto, 1L);
        } catch (NotFoundException o) {
            Assertions.assertEquals(o.getMessage(), "Предмет с id=22 не найден");
        }

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addBooking_whenEndEqualsStart_thenBadRequestException() {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2023, 11, 11, 11, 11))
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .build();

        try {
            bookingService.addBooking(bookingDto, 1L);
        } catch (BadRequestException o) {
            Assertions.assertEquals(o.getMessage(), "Поля времени начала и конца не должны быть одинвковыми");
        }

        Assertions.assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }


    @Test
    @SneakyThrows
    void addBooking_whenEndNull_thenBadRequestException() {

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .end(LocalDateTime.now())
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .build();


        try {
            bookingService.addBooking(bookingDto, 1L);
        } catch (BadRequestException o) {
            Assertions.assertEquals(o.getMessage(), "Поля времени начала и конца должны быть заполненны");
        }

        Assertions.assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addBooking_whenStartNull_thenBadRequestException() {

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .booker(UserDto.builder().build())
                .status(Status.WAITING)
                .item(ItemDto.builder().build())
                .build();


        try {
            bookingService.addBooking(bookingDto, 1L);
        } catch (BadRequestException o) {
            Assertions.assertEquals(o.getMessage(), "Поля времени начала и конца должны быть заполненны");
        }

        Assertions.assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto, 1L));

        verify(bookingRepository, never()).save(Mockito.any());
    }
}
