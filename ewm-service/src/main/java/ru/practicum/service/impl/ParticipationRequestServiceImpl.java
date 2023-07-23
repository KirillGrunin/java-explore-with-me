package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.ParticipationRequestService;
import ru.practicum.service.mapper.ParticipationRequestMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.util.LifeCycleState.PUBLISHED;
import static ru.practicum.util.StatusRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto saveParticipationRequest(Long userId, Long eventId) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        final User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (event.getInitiator().getId().equals(userId))
            throw new IllegalStateException("Инициатор события не может добавить запрос на участие в своём событии");
        if (!event.getState().equals(PUBLISHED))
            throw new IllegalStateException("Нельзя участвовать в неопубликованном событии");
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit()))
            throw new IllegalStateException("У события достигнут лимит запросов на участие");
        if (participationRequestRepository.existsByRequesterIdAndEventId(userId, eventId))
            throw new IllegalStateException("Запрос на событие уже существует");
        final ParticipationRequest request = ParticipationRequestMapper.toParticipationRequest(event, requester);
        if (request.getStatus().equals(CONFIRMED))
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        checkUser(userId);
        final ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден или недоступен"));
        request.setStatus(CANCELED);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequest(Long userId) {
        checkUser(userId);
        return participationRequestRepository.findAllByRequester_Id(userId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByEvent(Long userId, Long eventId) {
        checkUser(userId);
        checkEvent(eventId);
        return participationRequestRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatusParticipationRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        checkUser(userId);
        final Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0)
            throw new IllegalStateException("Лимит заявок равен 0 или отключена пре-модерация заявок");
        final List<Long> requestIds = request.getRequestIds();
        switch (request.getStatus()) {
            case REJECTED:
                return addStatusRejected(requestIds);
            case CONFIRMED:
                return addStatusConfirmed(requestIds, event);
            default:
                throw new IllegalStateException("Статус указан не верно");
        }
    }

    private EventRequestStatusUpdateResult addStatusRejected(List<Long> requestIds) {
        final List<ParticipationRequest> requests = participationRequestRepository.findAllById(requestIds);
        checkStatusPending(requests);
        requests.forEach(r -> r.setStatus(REJECTED));
        participationRequestRepository.saveAll(requests);
        List<ParticipationRequestDto> rejectedRequests = requests
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(List.of(), rejectedRequests);
    }

    private EventRequestStatusUpdateResult addStatusConfirmed(List<Long> requestsIds, Event event) {
        final int limit = event.getParticipantLimit();
        int confRequests = event.getConfirmedRequests();
        if (limit > 0 && confRequests == limit)
            throw new IllegalStateException("Достигнут лимит по заявкам на данное событие");
        List<ParticipationRequest> confirmedRequests;
        if (requestsIds.size() > (limit - confRequests)) {
            confirmedRequests = participationRequestRepository.findAllById(requestsIds
                    .stream()
                    .limit(limit - confRequests)
                    .collect(Collectors.toList()));
        } else {
            confirmedRequests = participationRequestRepository.findAllById(requestsIds);
        }
        checkStatusPending(confirmedRequests);
        for (ParticipationRequest req : confirmedRequests) {
            req.setStatus(CONFIRMED);
            confRequests++;
        }
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        List<Long> listId = confirmedRequests.stream().map(ParticipationRequest::getId).collect(Collectors.toList());
        if (limit == confRequests) {
            rejectedRequests = participationRequestRepository.findAllByEvent_IdAndIdNotInAndStatus(event.getId(), listId, PENDING)
                    .stream()
                    .peek(req -> req.setStatus(REJECTED))
                    .collect(Collectors.toList());
        }
        List<ParticipationRequest> updateRequests = new ArrayList<>(confirmedRequests);
        updateRequests.addAll(rejectedRequests);
        participationRequestRepository.saveAll(updateRequests);
        event.setConfirmedRequests(confRequests);
        eventRepository.save(event);
        return new EventRequestStatusUpdateResult(confirmedRequests
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList()), rejectedRequests
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));
    }

    private void checkStatusPending(List<ParticipationRequest> requests) {
        boolean isConfirmedRequest = requests
                .stream()
                .anyMatch(r -> !r.getStatus().equals(PENDING));
        if (isConfirmedRequest)
            throw new IllegalStateException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с идентификатором : " + userId + " не найден.");
        }
    }

    private void checkEvent(Long eventId) {
        if (!eventRepository.existsById(eventId))
            throw new NotFoundException("Событие не найдено");
    }
}