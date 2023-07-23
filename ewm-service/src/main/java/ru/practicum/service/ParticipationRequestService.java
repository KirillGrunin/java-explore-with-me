package ru.practicum.service;

import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto saveParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getAllParticipationRequest(Long userId);

    List<ParticipationRequestDto> getParticipationRequestsByEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusParticipationRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest request);
}