package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemWithBookingMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemWithBookingMapper.toItemWithBookingDto;


@Slf4j
@Service
@RestController
public class ItemService {
    private final ItemRepository itemStorage;
    private final BookingRepository bookingRepository;
    private final UserRepository userStorage;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final PageableCreater pageableCreater;

    public ItemService(ItemRepository itemStorage,
                       UserRepository userStorage,
                       BookingRepository bookingRepository,
                       CommentRepository commentRepository,
                       ItemRequestRepository itemRequestRepository,
                       PageableCreater pageableCreater) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.pageableCreater = pageableCreater;
    }

    public ItemDto addItem(ItemDto item, Long userId) throws NotFoundException, BadRequestException {
        if (item.getAvailable() == null) {
            throw new BadRequestException();
        }
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        Item itemNew = ItemMapper.toItem(item);
        itemNew.setOwner(user);
        if (item.getRequestId() != null) {
            itemNew.setRequest(itemRequestRepository.getById(item.getRequestId()));
        }
        return ItemMapper.toItemDto(itemStorage.save(itemNew));
    }

    public ItemDto updateItem(Long itemId, ItemDto item, Long userId) throws NotFoundException {
        if (!userId.equals(itemStorage.getById(itemId).getOwner().getId())) {
            throw new NotFoundException("У вас нет прав на обновление придмета с ID = " + itemId);
        }
        checkUserId(userId);
        Item updateItem = itemStorage.getById(itemId);
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.save(updateItem));
    }


    public ItemWithBookingDto getItem(Long itemId, Long userId) throws NotFoundException {
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Предмет с id= " + itemId + " не найден"));

        item.setComments(commentRepository.findCommentsByItemOrderByCreatedDesc(item));
        return toItemWithBookingDto(addItemBookings(item, userId));
    }

    public List<ItemWithBookingDto> getItems(Long id) throws NotFoundException {
        User user = userStorage.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с ID = " + id + " не найден"));

        List<Booking> bookings = bookingRepository.findByItemOwnerOrderByStartDesc(user);

        List<Item> items = itemStorage.getItemsByOwnerId(id);

        List<Item> ownerItems = new ArrayList<>();
        for (Item item : items) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            for (Booking booking : bookings) {
                if ((((booking.getEnd().isAfter(LocalDateTime.now())
                        && booking.getStart().isBefore(LocalDateTime.now()))
                        || booking.getEnd().isBefore(LocalDateTime.now()))
                        && booking.getStatus().equals(Status.APPROVED))
                        && Objects.equals(booking.getItem().getId(), item.getId())) {
                    lastBooking = booking;
                    break;
                }
            }
            if (lastBooking != null) {
                bookings.remove(lastBooking);
            }
            for (Booking booking : bookings) {
                if ((booking.getStart().isAfter(LocalDateTime.now())
                        && (booking.getStatus().equals(Status.WAITING)
                        || booking.getStatus().equals(Status.APPROVED)))
                        && Objects.equals(booking.getItem().getId(), item.getId())) {
                    if (nextBooking != null && nextBooking.getStart().isBefore(booking.getStart())) {
                        continue;
                    }
                    nextBooking = booking;
                }
            }
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
            ownerItems.add(item);
        }
        return ownerItems.stream().map(ItemWithBookingMapper::toItemWithBookingDto).collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text, Integer from, Integer size) throws BadRequestException {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable pageable = pageableCreater.doPageable(from, size);
        return itemStorage.search(text, pageable).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public void deleteItem(Long id) {
        itemStorage.deleteById(id);
    }

    private void checkUserId(Long userId) throws NotFoundException {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID = " + userId + " не найден");
        }
    }


    private Item addItemBookings(Item item, Long userId) {
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            List<Booking> itemBookings = bookingRepository.findByItemOrderByStart(item);

            for (Booking booking : itemBookings) {
                if ((booking.getEnd().isAfter(LocalDateTime.now()) &&
                        booking.getStart().isBefore(LocalDateTime.now())) ||
                        booking.getEnd().isBefore(LocalDateTime.now())) {
                    lastBooking = booking;
                }
            }

            if (lastBooking != null) {
                itemBookings.remove(lastBooking);
            }
            for (Booking booking : itemBookings) {
                if (booking.getStart().isAfter(LocalDateTime.now())
                        && (booking.getStatus().equals(Status.WAITING) || booking.getStatus().equals(Status.APPROVED))) {

                    nextBooking = booking;
                    break;
                }
            }
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
        }
        return item;
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) throws NotFoundException, BadRequestException {

        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Предмет с id= " + userId + " не найден"));
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Предмет с id= " + itemId + " не найден"));

        List<Booking> booking = bookingRepository.findByBookerAndItemAndEndBefore(user, item, LocalDateTime.now());

        if (commentDto.getText().isBlank()) {
            throw new BadRequestException("Коментарий не может быть пустым");
        }
        Comment comment = toComment(commentDto);
        if (!booking.isEmpty()) {
            comment.setAuthor(userStorage.getById(userId));
            comment.setItem(itemStorage.getById(itemId));
            comment.setCreated(LocalDateTime.now());
            return toCommentDto(commentRepository.save(comment));
        } else {
            throw new BadRequestException("Что бы оставить комментарий нужно забронировать предмет");
        }
    }

}
