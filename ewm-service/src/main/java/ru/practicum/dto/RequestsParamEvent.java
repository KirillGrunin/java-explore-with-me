package ru.practicum.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import ru.practicum.util.LifeCycleState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class RequestsParamEvent {
    private List<Long> users;
    private List<LifeCycleState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private PageRequest page;
    private String text;
    private Boolean paid;
    private Boolean onlyAvailable;
}