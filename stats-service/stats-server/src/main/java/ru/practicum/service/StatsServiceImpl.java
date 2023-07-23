package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DateException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void saveStats(EndpointHitDto endpointHit) {
        statsRepository.save(StatsMapper.toStats(endpointHit));
    }

    @Override
    public List<ViewStatsDto> getCountStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        validateDate(start, end);
        if (uris.isEmpty()) {
            if (unique)
                return statsRepository.getCountViewStatsWithUniqueIp(start, end);
            return statsRepository.getViewStatsCount(start, end);
        }
        if (unique)
            return statsRepository.getCountViewStatsFromListUriWithUniqueIp(start, end, uris);
        return statsRepository.getCountViewStatsFromListUri(start, end, uris);
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || start.isAfter(end))
            throw new DateException("Дата старта должна быть раньше даты окончания");
    }
}