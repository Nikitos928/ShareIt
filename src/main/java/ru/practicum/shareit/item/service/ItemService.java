package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundexception;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RestController
public class ItemService {
    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    public ItemService(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage, @Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public ItemDto addItem(ItemDto item, Map<String, String> headers) throws NotFoundexception, BadRequestException {
        if (item.getAvailable() == null) {
            throw new BadRequestException();
        }
        Long userId = Long.parseLong(headers.get("x-sharer-user-id"));
        checkUserId(userId);
        Item itemNew = ItemMapper.toItem(item);
        itemNew.setOwner(userId);
        return ItemMapper.toItemDto(itemStorage.addItem(itemNew));
    }

    public ItemDto updateItem(Long itemId, ItemDto item, Map<String, String> headers) throws NotFoundexception {
        Long userId = Long.parseLong(headers.get("x-sharer-user-id"));
        if (!userId.equals(itemStorage.getItemForStorage(itemId).getOwner())) {
            throw new NotFoundexception("У вас нет прав на обновление придмета с ID = " + itemId);
        }
        checkUserId(userId);
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, ItemMapper.toItem(item)));
    }

    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(itemStorage.getItem(id));
    }

    public List<ItemDto> getItems(Map<String, String> headers) {
        Long userId = Long.parseLong(headers.get("x-sharer-user-id"));
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemStorage.getItems(userId)) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }

        return itemDtos;
    }

    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemStorage.searchItem(text)) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    public void deleteItem(Long id) {
        itemStorage.deleteItem(id);
    }

    private void checkUserId(Long userId) throws NotFoundexception {
        if (!userStorage.getUserId().contains(userId)) {
            throw new NotFoundexception("Пользователь с ID = " + userId + " не найден");
        }
    }

}
