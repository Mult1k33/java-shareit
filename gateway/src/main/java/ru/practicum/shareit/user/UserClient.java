package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.*;

import java.util.List;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<List<UserDtoResponse>> getAllUsers() {
        ParameterizedTypeReference<List<UserDtoResponse>> typeRef =
                new ParameterizedTypeReference<List<UserDtoResponse>>() {};
        return get("", typeRef);
    }

    public ResponseEntity<UserDtoResponse> getUserById(Long userId) {
        return get("/" + userId, UserDtoResponse.class);
    }

    public ResponseEntity<UserDtoResponse> createUser(UserDtoRequest userDtoRequest) {
        return post("", userDtoRequest, UserDtoResponse.class);
    }

    public ResponseEntity<UserDtoResponse> updateUser(Long userId, UserUpdateDtoRequest userUpdateDtoRequest) {
        return patch("/" + userId, userUpdateDtoRequest, UserDtoResponse.class);
    }

    public ResponseEntity<Void> deleteUser(Long userId) {
        return delete("/" + userId, Void.class);
    }
}