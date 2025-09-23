package ru.practicum.shareit.client;

import jakarta.annotation.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {

    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<T> get(String path, Class<T> responseType) {
        return get(path, null, null, responseType);
    }

    protected <T> ResponseEntity<T> get(String path, long userId, Class<T> responseType) {
        return get(path, userId, null, responseType);
    }

    protected <T> ResponseEntity<T> get(String path, Long userId,
                                        @Nullable Map<String, Object> parameters, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null, responseType);
    }

    protected <T> ResponseEntity<T> get(String path, ParameterizedTypeReference<T> responseType) {
        return get(path, null, null, responseType);
    }

    protected <T> ResponseEntity<T> get(String path, long userId, ParameterizedTypeReference<T> responseType) {
        return get(path, userId, null, responseType);
    }

    protected <T> ResponseEntity<T> get(String path, Long userId,
                                        @Nullable Map<String, Object> parameters,
                                        ParameterizedTypeReference<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null, responseType);
    }

    protected <T, R> ResponseEntity<R> post(String path, T body, Class<R> responseType) {
        return post(path, null, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> post(String path, long userId, T body, Class<R> responseType) {
        return post(path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> post(String path, Long userId,
                                            @Nullable Map<String, Object> parameters,
                                            T body, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body, responseType);
    }

    protected <T, R> ResponseEntity<R> patch(String path, T body, Class<R> responseType) {
        return patch(path, null, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> patch(String path, long userId, T body, Class<R> responseType) {
        return patch(path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> patch(String path, Long userId,
                                             @Nullable Map<String, Object> parameters,
                                             T body, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body, responseType);
    }

    protected <T> ResponseEntity<T> delete(String path, Class<T> responseType) {
        return delete(path, null, null, responseType);
    }

    protected <T> ResponseEntity<T> delete(String path, long userId, Class<T> responseType) {
        return delete(path, userId, null, responseType);
    }

    protected <T> ResponseEntity<T> delete(String path, Long userId,
                                           @Nullable Map<String, Object> parameters,
                                           Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null, responseType);
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                        @Nullable Map<String, Object> parameters,
                                                        @Nullable T body, Class<R> responseType) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        try {
            ResponseEntity<R> shareitServerResponse;
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, responseType);
            }

            return prepareGatewayResponse(shareitServerResponse);

        } catch (HttpStatusCodeException e) {
            // Для всех ошибок возвращаем тело ошибки
            return ResponseEntity.status(e.getStatusCode())
                    .body((R) e.getResponseBodyAsString());
        }
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                        @Nullable Map<String, Object> parameters,
                                                        @Nullable T body, ParameterizedTypeReference<R> responseType) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        try {
            ResponseEntity<R> shareitServerResponse;
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, responseType);
            }

            return prepareGatewayResponse(shareitServerResponse);

        } catch (HttpStatusCodeException e) {
            // Для ошибок возвращаем тело как строку
            return (ResponseEntity<R>) ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static <T> ResponseEntity<T> prepareGatewayResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}