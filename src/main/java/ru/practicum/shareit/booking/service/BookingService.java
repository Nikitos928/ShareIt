package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundexception;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RestController
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;


    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public BookingDto addBooking(BookingDto booking, Long userId) throws NotFoundexception, BadRequestException {
        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new BadRequestException("Поля времени начала и конца должны быть заполненны");
        }

        if (booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Поля времени начала и конца не должны быть одинвковыми");
        }

        checkUserId(userId);

        checkItemById(booking.getItemId());
        if (!itemRepository.getById(booking.getItemId()).getAvailable()) {
            throw new BadRequestException("Предмет нельзя забронировать");
        }
        if (booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getStart().isAfter(booking.getEnd()) ||
                booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Некорректное время");
        }
        if (Objects.equals(itemRepository.getById(booking.getItemId()).getOwner().getId(), userId)) {
            throw new NotFoundexception("Владелец вещинне может забронировать предмет");
        }
        Booking newBooking = BookingMapper.toBooking(booking);
        newBooking.setBooker(userRepository.getById(userId));
        newBooking.setItem(itemRepository.getById(booking.getItemId()));
        newBooking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Transactional
    public BookingDto bookingApproving(Long bookingId, Boolean isApproved, Long userId) throws BadRequestException, NotFoundexception {
        var owner = userRepository.getById(userId);
        var booking = bookingRepository.getById(bookingId);
        var item = itemRepository.getById(booking.getItem().getId());
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже одобрино");
        }
        if (item.getOwner() != owner) {
            throw new NotFoundexception("Подтведить может только владелец");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


    public Booking getById(Long bookingId, Long userId) throws NotFoundexception {
        var user = userRepository.getById(userId);
        checkBookingById(bookingId);
        var booking = bookingRepository.getById(bookingId);
        if (booking.getBooker() != user && booking.getItem().getOwner() != user) {
            throw new NotFoundexception("Только владелец или арендатор может получить информацию о бронировании");
        }
        return booking;
    }


    public List<BookingDto> getAllBookingsByUser(String state, Long userId) throws NotFoundexception {
        checkUserId(userId);
        User user = userRepository.getById(userId);
        try {
            switch (checkState(state)) {
                case CURRENT:
                    return bookingRepository.findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now(),
                            LocalDateTime.now()).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findBookingsByBookerAndEndBeforeOrderByStartDesc(
                            user,
                            LocalDateTime.now()).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findBookingsByBookerAndStartAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now()).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(user, Status.WAITING)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(user, Status.REJECTED)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                default:
                    return bookingRepository.findBookingsByBookerOrderByStartDesc(user)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<BookingDto> getAllBookingItemsByUser(String state, Long userId) throws NotFoundexception {

        checkUserId(userId);
        User user = userRepository.getById(userId);
        try {
            switch (checkState(state)) {
                case CURRENT:
                    return bookingRepository.findBookingsByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now(),
                            LocalDateTime.now()).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findBookingsByItem_OwnerAndEndBeforeOrderByStartDesc(
                            user,
                            LocalDateTime.now()).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findBookingsByItem_OwnerAndStartAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now()).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(user, Status.WAITING)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(user, Status.REJECTED)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                default:
                    return bookingRepository.findBookingsByItem_OwnerOrderByStartDesc(user)
                            .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkUserId(Long id) throws NotFoundexception {
        if (!userRepository.findAll().stream().map(User::getId).collect(Collectors.toList()).contains(id)) {
            throw new NotFoundexception("Пользователь с id= " + id + " не найден!");
        }
    }

    private void checkItemById(Long itemId) throws NotFoundexception {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundexception("Предмет с id=" + itemId + " не найден");
        }
    }

    private void checkBookingById(Long bookingId) throws NotFoundexception {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundexception("Бронирование с id=" + bookingId + " не найдено");
        }
    }

    private State checkState(String state) throws InternalServerErrorException {
        State enumState;
        try {
            enumState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InternalServerErrorException("Unknown state: " + state);
        }
        return enumState;
    }

}
