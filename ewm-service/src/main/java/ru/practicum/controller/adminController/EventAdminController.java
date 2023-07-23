package ru.practicum.controller.adminController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.RequestsParamEvent;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.service.EventService;
import ru.practicum.util.LifeCycleState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateAdminEvent(@PathVariable Long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest eventAdminRequest) {
        log.debug("Обновлено событие с идентификатором: {}", eventId);
        return eventService.updateAdminEvent(eventId, eventAdminRequest);
    }

    @GetMapping
    public List<EventFullDto> getAdminAllEvent(@RequestParam(required = false) List<Long> users,
                                               @RequestParam(required = false) List<LifeCycleState> states,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                               @Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        final RequestsParamEvent requestsParamEvent = RequestsParamEvent.builder()
                .categories(categories)
                .users(users)
                .states(states)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .page(page)
                .build();
        log.debug("Получен список событий для администратора");
        return eventService.getAdminAllEvent(requestsParamEvent);
    }
}