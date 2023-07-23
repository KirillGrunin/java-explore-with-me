package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> getAllEvents(Long userId, PageRequest of);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest eventAdminRequest);

    List<EventFullDto> getAdminAllEvent(RequestsParamEvent requestsParamEvent);

    List<EventShortDto> getEvents(RequestsParamEvent requestsParamEvent, HttpServletRequest request, String app, String sort);

    EventFullDto getFullEventById(Long id, HttpServletRequest request, String app);
}