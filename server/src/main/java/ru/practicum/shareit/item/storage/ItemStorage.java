package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    public Item addItem(Item item);

    public Item updateItem(Long itemId, Item item);

    public List<Item> getItems(Long userId);

    public Item getItem(Long id);

    public void deleteItem(Long id);

    public List<Item> searchItem(String text);


}
