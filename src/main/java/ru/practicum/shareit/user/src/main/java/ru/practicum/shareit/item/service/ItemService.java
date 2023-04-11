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

    public ItemDto addItem(ItemDto item, Long userId) throws NotFoundexception, BadRequestException {
        if (item.getAvailable() == null) {
            throw new BadRequestException();
        }
        checkUserId(userId);
        Item itemNew = ItemMapper.toItem(item);
        itemNew.setOwner(userId);
        return ItemMapper.toItemDto(itemStorage.addItem(itemNew));
    }

    public ItemDto updateItem(Long itemId, ItemDto item, Long userId) throws NotFoundexception {
        if (!userId.equals(itemStorage.getItem(itemId).getOwner())) {
            throw new NotFoundexception("У вас нет прав на обновление придмета с ID = " + itemId);
        }
        checkUserId(userId);
        Item updateItem = itemStorage.getItem(itemId);
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, updateItem));
    }

    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(itemStorage.getItem(id));
    }

    public List<ItemDto> getItems(Long userId) {
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
