package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.ViewStatsDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected List<ViewStatsDto> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> List<ViewStatsDto> post(String path, T body) {
        return post(path, null, body);
    }

    protected <T> List<ViewStatsDto> post(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    private <T> List<ViewStatsDto> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<List<ViewStatsDto>> ewmServerResponse;
        try {
            if (parameters != null) {
                ewmServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<>() {
                }, parameters);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<>() {
                });
            }
        } catch (Exception exception) {
            return Collections.emptyList();
        }
        return prepareStatsServiceResponse(ewmServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static List<ViewStatsDto> prepareStatsServiceResponse(ResponseEntity<List<ViewStatsDto>> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody()).getBody();
        }
        return Collections.emptyList();
    }
}