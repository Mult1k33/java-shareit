package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.*;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getAllItemsByOwner(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDtoRequest itemDtoRequest) {
        return post("", userId, itemDtoRequest);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateDtoRequest itemUpdateDtoRequest) {
        return patch("/" + itemId, userId, itemUpdateDtoRequest);
    }

    public void deleteItem(Long userId, Long itemId) {
        delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchByText(Long userId, String text) {
        return get("/search?text={text}", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDtoRequest commentDtoRequest) {
        return post("/" + itemId + "/comment", userId, commentDtoRequest);
    }

    public ResponseEntity<Object> getAllComments(Long userId, Long itemId) {
        return get("/" + itemId + "/comment", userId);
    }
}