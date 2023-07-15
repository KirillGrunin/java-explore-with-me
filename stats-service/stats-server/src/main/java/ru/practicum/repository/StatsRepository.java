package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(s.app, s.uri, count(s.id))" +
            " FROM Stats s" +
            " WHERE s.timestamp BETWEEN :start AND :end" +
            " GROUP BY s.app, s.uri" +
            " ORDER BY count(s.id) DESC")
    List<ViewStatsDto> getViewStatsCount(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(s.app, s.uri, count(distinct s.ip))" +
            " FROM Stats s" +
            " WHERE s.timestamp BETWEEN :start AND :end" +
            " GROUP BY s.app, s.uri" +
            " ORDER BY count(s.id) DESC")
    List<ViewStatsDto> getCountViewStatsWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(s.app, s.uri, count(s.id))" +
            " FROM Stats s" +
            " WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris" +
            " GROUP BY s.app, s.uri" +
            " ORDER BY count(s.id) DESC")
    List<ViewStatsDto> getCountViewStatsFromListUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(s.app, s.uri, count(distinct s.ip))" +
            " FROM Stats s" +
            " WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris" +
            " GROUP BY s.app, s.uri" +
            " ORDER BY count(s.id) DESC")
    List<ViewStatsDto> getCountViewStatsFromListUriWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}