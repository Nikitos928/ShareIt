package ru.practicum.shareit.item.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("InMemoryItemStorage")

public class InMemoryItemStorage implements ItemStorage {

    ItemMapper itemMapper = new ItemMapper();

    Map<Long, Item> items = new HashMap<>();

    private Long id = 1L;

    @Override
    public Item addItem(Item item) {
        item.setId(id);
        items.put(id, item);
        id++;
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getItems(Long userId) {

        return new ArrayList<>(items.values().stream()
                .filter(t -> Objects.equals(t.getOwner(), userId))
                .collect(Collectors.toSet()));
    }



    @Override
    public Item getItem(Long id) {
        return items.get(id);
    }

    @Override
    public void deleteItem(Long id) {
        items.remove(id);
    }

    public List<Item> searchItem(String text) {
        text = text.toLowerCase();
        Set<Item> item = new HashSet<>();
        for (Item value : items.values()) {
            if (value.getName().toLowerCase().contains(text) && value.getAvailable()) {
                item.add(value);
                continue;
            }
            if (value.getDescription().toLowerCase().contains(text) && value.getAvailable()) {
                item.add(value);
            }
        }
        return item.stream().sorted(Comparator.comparingLong(Item::getId)).distinct().collect(Collectors.toList());
    }


}
