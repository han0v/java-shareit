package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return items.get(id);
    }

    public Map<Long, Item> findAll() {
        return new HashMap<>(items);
    }

    public void deleteById(Long id) {
        items.remove(id);
    }

    public Map<Long, Item> findAllByOwner(Long ownerId) {
        Map<Long, Item> result = new HashMap<>();
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            if (entry.getValue().getOwner().getId().equals(ownerId)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public Map<Long, Item> searchAvailableItems(String text) {
        Map<Long, Item> result = new HashMap<>();
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            Item item = entry.getValue();
            if (item.getAvailable() &&
                    (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}