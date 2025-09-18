package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.comment.dto.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;
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

    public ResponseEntity<List<ItemDtoResponse>> getAllItemsByOwner(Long userId) {
        ParameterizedTypeReference<List<ItemDtoResponse>> typeRef =
                new ParameterizedTypeReference<List<ItemDtoResponse>>() {};
        return get("", userId, typeRef);
    }

    public ResponseEntity<ItemFullDtoResponse> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId, ItemFullDtoResponse.class);
    }

    public ResponseEntity<ItemDtoResponse> createItem(Long userId, ItemDtoRequest itemDtoRequest) {
        return post("", userId, itemDtoRequest, ItemDtoResponse.class);
    }

    public ResponseEntity<ItemDtoResponse> updateItem(Long userId, Long itemId,
                                                      ItemUpdateDtoRequest itemUpdateDtoRequest) {
        return patch("/" + itemId, userId, itemUpdateDtoRequest, ItemDtoResponse.class);
    }

    public ResponseEntity<Void> deleteItem(Long userId, Long itemId) {
        return delete("/" + itemId, userId, Void.class);
    }

    public ResponseEntity<List<ItemDtoResponse>> searchByText(Long userId, String text) {
        ParameterizedTypeReference<List<ItemDtoResponse>> typeRef =
                new ParameterizedTypeReference<List<ItemDtoResponse>>() {};
        return get("/search?text={text}", userId, Map.of("text", text), typeRef);
    }

    public ResponseEntity<CommentDtoResponse> addComment(Long userId, Long itemId,
                                                         CommentDtoRequest commentDtoRequest) {
        return post("/" + itemId + "/comment", userId, commentDtoRequest, CommentDtoResponse.class);
    }

    public ResponseEntity<List<CommentDtoResponse>> getAllComments(Long userId, Long itemId) {
        ParameterizedTypeReference<List<CommentDtoResponse>> typeRef =
                new ParameterizedTypeReference<List<CommentDtoResponse>>() {};
        return get("/" + itemId + "/comment", userId, typeRef);
    }
}