package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundexception;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemWithBookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RestController
public class ItemService {
    private final ItemRepository itemStorage;
    private final BookingRepository bookingRepository;
    private final UserRepository userStorage;
    private final CommentRepository commentRepository;

    public ItemService(ItemRepository itemStorage,
                       UserRepository userStorage,
                       BookingRepository bookingRepository,
                       CommentRepository commentRepository) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public ItemDto addItem(ItemDto item, Long userId) throws NotFoundexception, BadRequestException {
        if (item.getAvailable() == null) {
            throw new BadRequestException();
        }
        checkUserId(userId);
        Item itemNew = ItemMapper.toItem(item);
        itemNew.setOwner(userStorage.getById(userId));
        return ItemMapper.toItemDto(itemStorage.save(itemNew));
    }

    public ItemDto updateItem(Long itemId, ItemDto item, Long userId) throws NotFoundexception {
        if (!userId.equals(itemStorage.getById(itemId).getOwner().getId())) {
            throw new NotFoundexception("У вас нет прав на обновление придмета с ID = " + itemId);
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


    public Item getItem(Long itemId, Long userId) throws NotFoundexception {
        checkItemId(itemId);
        Item item = itemStorage.getById(itemId);
        try {
            item.setComments(commentRepository.findCommentsByItemOrderByCreatedDesc(item));
            item = addItemBookings(item, userId);
        } finally {
            return item;
        }
    }

    public List<ItemWithBookingDto> getItems(Long id) throws NotFoundexception {
        checkUserId(id);
        List<Item> items = itemStorage.findAll();
        List<Item> ownerItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().getId().equals(id)) {
                ownerItems.add(addItemBookings(item, id));
            }
        }
        return ownerItems.stream().map(ItemWithBookingMapper::toItemWithBookingDto).collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemStorage.search(text)) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    public void deleteItem(Long id) {
        itemStorage.deleteById(id);
    }

    private void checkUserId(Long userId) throws NotFoundexception {
        if (!userStorage.findAll().stream().map(User::getId).collect(Collectors.toList()).contains(userId)) {
            throw new NotFoundexception("Пользователь с ID = " + userId + " не найден");
        }
    }

    private void checkItemId(Long id) throws NotFoundexception {
        if (!itemStorage.findAll().stream().map(Item::getId).collect(Collectors.toList()).contains(id)) {
            throw new NotFoundexception("Предмет с id= " + id + " не найден");
        }
    }

    private Item addItemBookings(Item item, Long userId) {
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            List<Booking> itemBookings = bookingRepository.findBookingsByItemOrderByStart(item);

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
    public Comment createComment(Comment comment, Long itemId, Long userId) throws NotFoundexception, BadRequestException {
        checkItemId(itemId);
        checkUserId(userId);

        List<Booking> booking = bookingRepository.findBookingByBookerAndItemAndEndBefore(
                userStorage.getById(userId),
                itemStorage.getById(itemId),
                LocalDateTime.now());

        if (comment.getText().isBlank()) {
            throw new BadRequestException("Коментарий не может быть пустым");
        }

        if (!booking.isEmpty()) {
            comment.setAuthor(userStorage.getById(userId));
            comment.setItem(itemStorage.getById(itemId));
            comment.setCreated(LocalDateTime.now());
            return commentRepository.save(comment);
        } else {
            throw new BadRequestException("Что бы оставить комментарий нужно забронировать предмет");
        }
    }

}
