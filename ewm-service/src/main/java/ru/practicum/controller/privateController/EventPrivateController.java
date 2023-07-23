package ru.practicum.controller.privateController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;
import ru.practicum.service.ParticipationRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;
    private final ParticipationRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable Long userId,
                                  @Valid @RequestBody NewEventDto newEventDto) {
        log.debug("Добавлено новое событие");
        return eventService.saveEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest eventUserRequest) {
        log.debug("Обновлено событие с идентификатором: {}", eventId);
        return eventService.updateEvent(userId, eventId, eventUserRequest);
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(@PathVariable Long userId,
                                            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.debug("Получен список событий");
        return eventService.getAllEvents(userId, PageRequest.of(from > 0 ? from / size : 0, size));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.debug("Получено событие с идентификатором: {}", eventId);
        return eventService.getEventById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsByEvent(@PathVariable Long userId,
                                                                         @PathVariable Long eventId) {
        log.debug("Получен список с запросами на участие в событии текущего пользователя");
        return service.getParticipationRequestsByEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusParticipationRequests(@PathVariable Long userId,
                                                                            @PathVariable Long eventId,
                                                                            @RequestBody EventRequestStatusUpdateRequest request) {
        log.debug("Обновлены статусы заявок на участие");
        return service.updateStatusParticipationRequests(userId, eventId, request);
    }
}