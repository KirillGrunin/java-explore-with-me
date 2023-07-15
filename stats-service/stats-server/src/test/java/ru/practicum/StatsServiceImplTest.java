package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceImplTest {

    @Mock
    private final StatsRepository statsRepository;

    private final StatsService statsService;

    @Test
    void shouldSaveStats() {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("ewm-service");
        endpointHitDto.setUri("/events/1");
        endpointHitDto.setIp("1.0.0.1.1.2");
        endpointHitDto.setTimestamp(LocalDateTime.now());

        when(statsRepository.save(any()))
                .thenReturn(Optional.empty());

        statsService.saveStats(endpointHitDto);

        verify(statsRepository, times(0)).save(any());
    }

    @Test
    void getStats() {
        when(statsRepository.getViewStatsCount(any(), any()))
                .thenReturn(Collections.emptyList());

        var result = statsService.getCountStats(LocalDateTime.now(), LocalDateTime.now().plusDays(1), List.of(""), false);

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(statsRepository, times(0)).getViewStatsCount(any(), any());

        when(statsRepository.getCountViewStatsWithUniqueIp(any(), any()))
                .thenReturn(Collections.emptyList());

        var result1 = statsService.getCountStats(LocalDateTime.now(), LocalDateTime.now().plusDays(1), List.of(""), true);

        assertThat(result1, notNullValue());
        assertThat("isEmpty", result1.isEmpty());
        verify(statsRepository, times(0)).getCountViewStatsWithUniqueIp(any(), any());

        when(statsRepository.getCountViewStatsFromListUri(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        var result2 = statsService.getCountStats(LocalDateTime.now(), LocalDateTime.now().plusDays(1), List.of("/t"), false);

        assertThat(result2, notNullValue());
        assertThat("isEmpty", result2.isEmpty());
        verify(statsRepository, times(0)).getCountViewStatsFromListUri(any(), any(), any());

        when(statsRepository.getCountViewStatsFromListUriWithUniqueIp(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        var result3 = statsService.getCountStats(LocalDateTime.now(), LocalDateTime.now().plusDays(1), List.of("/t"), true);

        assertThat(result3, notNullValue());
        assertThat("isEmpty", result3.isEmpty());
        verify(statsRepository, times(0)).getCountViewStatsFromListUri(any(), any(), any());
    }
}