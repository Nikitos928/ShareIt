package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotEmpty;

/**
 * TODO Sprint add-controllers.
 */

@Builder
@Value
public class ItemDto {
    Long id;
    @NotEmpty
    String name;
    @NotEmpty
    String description;
    Boolean available;
}
