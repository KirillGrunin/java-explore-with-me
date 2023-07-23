package ru.practicum.service.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.util.LifeCycleState;
import ru.practicum.util.State;

import java.time.LocalDateTime;

import static ru.practicum.util.LifeCycleState.*;

@UtilityClass
public class EventMapper {
    public Event toEvent(NewEventDto newEventDto, User user, Category category) {
        final Event event = new Event();
        event.setEventDate(newEventDto.getEventDate());
        event.setCategory(category);
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setTitle(newEventDto.getTitle());
        event.setState(LifeCycleState.PENDING);
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());
        return event;
    }

    public EventFullDto toEventFullDto(Event event) {
        final EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setState(event.getState());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        return eventFullDto;
    }

    public Event toEventUpdate(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null)
            event.setEventDate(updateEventUserRequest.getEventDate());
        if (updateEventUserRequest.getAnnotation() != null)
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        if (updateEventUserRequest.getDescription() != null)
            event.setDescription(updateEventUserRequest.getDescription());
        if (updateEventUserRequest.getPaid() != null)
            event.setPaid(updateEventUserRequest.getPaid());
        if (updateEventUserRequest.getParticipantLimit() != null)
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        if (updateEventUserRequest.getEventDate() != null)
            event.setCreatedOn(updateEventUserRequest.getEventDate());
        if (updateEventUserRequest.getTitle() != null)
            event.setTitle(updateEventUserRequest.getTitle());
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(State.CANCEL_REVIEW))
                event.setState(CANCELED);
            if (updateEventUserRequest.getStateAction().equals(State.SEND_TO_REVIEW))
                event.setState(PENDING);
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLat(updateEventUserRequest.getLocation().getLat());
            event.setLon(updateEventUserRequest.getLocation().getLon());
        }
        return event;
    }

    public EventShortDto toEventShortDto(Event event) {
        final EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setId(event.getId());
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        return eventShortDto;
    }

    public Event toEventAdminUpdate(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null)
            event.setEventDate(updateEventAdminRequest.getEventDate());
        if (updateEventAdminRequest.getAnnotation() != null)
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        if (updateEventAdminRequest.getDescription() != null)
            event.setDescription(updateEventAdminRequest.getDescription());
        if (updateEventAdminRequest.getPaid() != null)
            event.setPaid(updateEventAdminRequest.getPaid());
        if (updateEventAdminRequest.getParticipantLimit() != null)
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        if (updateEventAdminRequest.getEventDate() != null)
            event.setCreatedOn(updateEventAdminRequest.getEventDate());
        if (updateEventAdminRequest.getTitle() != null)
            event.setTitle(updateEventAdminRequest.getTitle());
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLat(updateEventAdminRequest.getLocation().getLat());
            event.setLon(updateEventAdminRequest.getLocation().getLon());
        }
        return event;
    }
}