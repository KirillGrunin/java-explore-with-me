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
import ru.practicum.dto.ViewStatsDto;

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

    @Value("${stats-service.url}")
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

    public List<ViewStatsDto> getStatsCount(List<String> listUris, LocalDateTime dateStart, LocalDateTime dateEnd) {
        final String uris = String.join(",", listUris);
        final String start = encode(dateStart);
        final String end = encode(dateEnd);
        final Boolean unique = true;
        final Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );

        final ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(url + "/stats?start={start}&end={end}&uris={uris}&unique={unique}", ViewStatsDto[].class, parameters);
        log.debug("Ответ от stats-server: {}", response);
        return List.of(Objects.requireNonNull(response.getBody()));
    }

    private String encode(LocalDateTime date) {
        return date.format(FORMAT);
    }
}