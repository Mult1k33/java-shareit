package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.*;

import java.util.List;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<ItemRequestDtoResponse> createRequest(Long userId, ItemRequestDtoRequest requestDtoRequest) {
        return post("", userId, requestDtoRequest, ItemRequestDtoResponse.class);
    }

    public ResponseEntity<List<ItemRequestDtoResponse>> getAllRequests(Long userId) {
        ParameterizedTypeReference<List<ItemRequestDtoResponse>> typeRef =
                new ParameterizedTypeReference<List<ItemRequestDtoResponse>>() {};
        return get("/all", userId, typeRef);
    }

    public ResponseEntity<List<ItemRequestDtoResponse>> getRequestByUser(Long userId) {
        ParameterizedTypeReference<List<ItemRequestDtoResponse>> typeRef =
                new ParameterizedTypeReference<List<ItemRequestDtoResponse>>() {};
        return get("", userId, typeRef);
    }

    public ResponseEntity<ItemRequestDtoResponse> getRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId, ItemRequestDtoResponse.class);
    }
}