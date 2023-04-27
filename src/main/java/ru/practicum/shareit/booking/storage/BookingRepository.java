package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerOrderByStartDesc(User user);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime localDateTime);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User user, Status status);

    List<Booking> findByItemOwnerOrderByStartDesc(User user);

    List<Booking> findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerAndEndBeforeOrderByStartDesc(User user, LocalDateTime localDateTime);

    List<Booking> findByItemOwnerAndStatusOrderByStartDesc(User user, Status status);

    List<Booking> findBookingByBookerAndItemAndEndBefore(User user, Item item, LocalDateTime localDateTime);

    List<Booking> findByItemOrderByStart(Item item);

    List<Booking> findByItemOwnerAndStartAfterOrderByStartDesc(User user, LocalDateTime localDateTime);

}
