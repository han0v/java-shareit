package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAllRequestsByUser(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        Map<String, Object> parameters = Map.of("from", 0, "size", 10); // Пример параметров пагинации
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}