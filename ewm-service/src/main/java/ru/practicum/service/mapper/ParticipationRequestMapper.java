package ru.practicum.service.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;

import java.time.LocalDateTime;

import static ru.practicum.util.StatusRequest.CONFIRMED;
import static ru.practicum.util.StatusRequest.PENDING;

@UtilityClass
public class ParticipationRequestMapper {
    public ParticipationRequest toParticipationRequest(Event event, User user) {
        final ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        request.setEvent(event);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(CONFIRMED);
        } else {
            request.setStatus(PENDING);
        }
        return request;
    }

    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        final ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(participationRequest.getId());
        requestDto.setCreated(participationRequest.getCreated());
        requestDto.setEvent(participationRequest.getEvent().getId());
        requestDto.setRequester(participationRequest.getRequester().getId());
        requestDto.setStatus(participationRequest.getStatus());
        return requestDto;
    }
}