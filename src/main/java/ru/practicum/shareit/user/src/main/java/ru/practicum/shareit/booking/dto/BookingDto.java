package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@Value
public class BookingDto {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    Integer item;
    Integer booker;
    Status status;
}
