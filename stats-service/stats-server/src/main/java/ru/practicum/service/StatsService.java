package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveStats(EndpointHitDto endpointHit);

    List<ViewStatsDto> getCountStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}