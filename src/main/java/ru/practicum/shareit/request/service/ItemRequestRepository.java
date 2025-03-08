package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private long nextId = 1;

    public ItemRequest save(ItemRequest request) {
        if (request.getId() == null) {
            request.setId(nextId++);
        }
        requests.put(request.getId(), request);
        return request;
    }

    public ItemRequest findById(Long id) {
        return requests.get(id);
    }

    public Map<Long, ItemRequest> findAll() {
        return new HashMap<>(requests);
    }

    public void deleteById(Long id) {
        requests.remove(id);
    }

    public List<ItemRequest> findAllByRequestor(User requestor) {
        List<ItemRequest> result = new ArrayList<>();
        for (ItemRequest request : requests.values()) {
            if (request.getRequestor().equals(requestor)) {
                result.add(request);
            }
        }
        return result;
    }
}