package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @NotBlank
    @Size(max = 111)
    private String name;
    @NotBlank
    @Size(max = 1111)
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
