package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@Value
public class ItemRequestDto {
    Long id;
    String description;
    Long requestor;
    LocalDateTime created;
}
