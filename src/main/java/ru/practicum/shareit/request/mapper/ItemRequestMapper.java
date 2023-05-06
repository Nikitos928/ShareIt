package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<ItemDto> itemsDto = new ArrayList<>();
        if (itemRequest.getItemList() != null) {
            for (Item item : itemRequest.getItemList()) {
                itemsDto.add(toItemDto(item));
            }
        }
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemsDto)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequest) {
        return ItemRequest.builder()
                .description(itemRequest.getDescription())
                .created(LocalDateTime.now())
                .build();
    }
}
