package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-service.url}") String url, RestTemplateBuilder restTemplateBuilder) {
        super(
                restTemplateBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void saveStats(HttpServletRequest request, String app) {
        final EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(app);
        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());

        post("/hit", endpointHitDto);
        log.debug("Статистика сохранена");
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
        log.debug("Получен ответ сервиса статистики");
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    private String encode(LocalDateTime date) {
        return date.format(FORMAT);
    }
}