package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsClientTwo {
    private final RestTemplate restTemplate;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${stats-server.url}")
    private String url;

    public void saveStats(HttpServletRequest request, String app) {
        final EndpointHitDto endpointHit = new EndpointHitDto();
        endpointHit.setApp(app);
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setTimestamp(LocalDateTime.now());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        final HttpEntity<EndpointHitDto> requestToStats = new HttpEntity<>(endpointHit, headers);

        final ResponseEntity<Void> response = restTemplate.postForEntity(url + "/hit", requestToStats, Void.class);
        log.debug("Ответ от stats-server: {}", response);
    }

    public ResponseEntity<Object> getStatsCount(String start, String end, List<String> uris, Boolean unique) {
        String joinUrl = String.join(",", uris);
        final Map<String, Object> parameters = Map.of(
                "start", encode(start),
                "end", encode(end),
                "uris", joinUrl,
                "unique", unique
        );

        final ResponseEntity<Object> response = restTemplate.getForEntity(url + "/stats", Object.class, parameters);
        log.debug("Ответ от stats-server: {}", response);
        return response;
    }

    private String encode(String date) {
        LocalDateTime time = LocalDateTime.parse(date, FORMAT);
        return time.format(FORMAT);
    }
}