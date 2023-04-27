package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Builder
@Value

public class BookingForItemDto {
    Long id;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
    Status status;
}
