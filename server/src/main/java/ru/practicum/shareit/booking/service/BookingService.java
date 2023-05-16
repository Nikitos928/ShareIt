package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final PageableCreater pageableCreater;


    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository,
                          PageableCreater pageableCreater) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.pageableCreater = pageableCreater;
    }

    @Transactional
    public BookingDto addBooking(BookingDto booking, Long userId) throws NotFoundException, BadRequestException {
        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new BadRequestException("Поля времени начала и конца должны быть заполненны");
        }

        if (booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Поля времени начала и конца не должны быть одинвковыми");
        }

        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(
                () -> new NotFoundException("Предмет с id=" + booking.getItemId() + " не найден"));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + booking.getItemId() + " не найден");
        }

        if (!item.getAvailable()) {
            throw new BadRequestException("Предмет нельзя забронировать");
        }
        if (booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getStart().isAfter(booking.getEnd()) ||
                booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Некорректное время");
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Владелец вещинне может забронировать предмет");
        }
        Booking newBooking = BookingMapper.toBooking(booking);
        newBooking.setBooker(userRepository.getById(userId));
        newBooking.setItem(item);
        newBooking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Transactional
    public BookingDto bookingApproving(Long bookingId, Boolean isApproved, Long userId) throws BadRequestException, NotFoundException {
        var owner = userRepository.getById(userId);
        var booking = bookingRepository.getById(bookingId);
        var item = itemRepository.getById(booking.getItem().getId());
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Бронирование уже одобрино");
        }
        if (item.getOwner() != owner) {
            throw new NotFoundException("Подтведить может только владелец");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


    public BookingDto getById(Long bookingId, Long userId) throws NotFoundException {
        var user = userRepository.getById(userId);
        checkBookingById(bookingId);
        var booking = bookingRepository.getById(bookingId);
        if (booking.getBooker() != user && booking.getItem().getOwner() != user) {
            throw new NotFoundException("Только владелец или арендатор может получить информацию о бронировании");
        }
        return BookingMapper.toBookingDto(booking);
    }


    public List<BookingDto> getAllBookingsByUser(String state, Long userId, Integer from, Integer size) throws NotFoundException, InvalidStateException, BadRequestException {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id= " + userId + " не найден!"));

        Pageable pageable = pageableCreater.doPageable(from, size);
        switch (checkState(state)) {
            case CURRENT:
                return bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                        user,
                        LocalDateTime.now(),
                        LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .sorted(Comparator.comparing(BookingDto ::getId)) // отсортировал для postman
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(
                        user,
                        LocalDateTime.now(), pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerAndStartAfterOrderByStartDesc(
                        user,
                        LocalDateTime.now(), pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBookerAndStatusOrderByStartDesc(user, Status.WAITING, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBookerAndStatusOrderByStartDesc(user, Status.REJECTED, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                return bookingRepository.findByBookerOrderByStartDesc(user, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }

    }


    public List<BookingDto> getAllBookingsByOwner(String state, Long userId, Integer from, Integer size) throws NotFoundException, InvalidStateException, BadRequestException {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id= " + userId + " не найден!"));
        Pageable pageable = pageableCreater.doPageable(from, size);
        switch (checkState(state)) {
            case CURRENT:
                return bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                        user,
                        LocalDateTime.now(),
                        LocalDateTime.now(), pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(
                        user,
                        LocalDateTime.now(), pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByItemOwnerAndStartAfterOrderByStartDesc(
                        user,
                        LocalDateTime.now(), pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(user, Status.WAITING, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByItemOwnerAndStatusOrderByStartDesc(user, Status.REJECTED, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                return bookingRepository.findByItemOwnerOrderByStartDesc(user, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }

    private void checkBookingById(Long bookingId) throws NotFoundException {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Бронирование с id=" + bookingId + " не найдено");
        }
    }

    private State checkState(String state) throws InvalidStateException {
        State enumState;
        try {
            enumState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Unknown state: " + state);
        }
        return enumState;
    }

}
