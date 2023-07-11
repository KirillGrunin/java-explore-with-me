package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveStats(@RequestBody @Valid EndpointHitDto endpointHit) {
        log.debug("Информация сохранена");
        statsService.saveStats(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatsCount(@RequestParam(value = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                            @RequestParam(value = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                            @RequestParam(value = "uris", required = false, defaultValue = "") List<String> uris,
                                            @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique) {
        log.debug("Получена статистика для списка uri: {}", uris);
        return statsService.getCountStats(start, end, uris, unique);
    }
}