package ru.practicum.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClientTwo;
import ru.practicum.dto.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.QEvent;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.EventService;
import ru.practicum.service.mapper.EventMapper;
import ru.practicum.util.StateAction;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.service.mapper.EventMapper.*;
import static ru.practicum.util.LifeCycleState.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClientTwo statsClient;


    @Override
    @Transactional
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalStateException("Дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }
        final User user = getUserRepository(userId);
        final Category category = chekCategory(newEventDto.getCategory());
        final Event event = toEvent(newEventDto, user, category);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        checkUser(userId);
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getState().equals(PUBLISHED) || event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalStateException("Статус должен быть PENDING или CANCELED и дата и время на которые" +
                    " намечено событие не может быть раньше,чем через два часа от текущего момента");
        }
        final Event eventUpdate = toEventUpdate(event, updateEventUserRequest);
        if (updateEventUserRequest.getCategory() != null) {
            final Category category = chekCategory(updateEventUserRequest.getCategory());
            eventUpdate.setCategory(category);
        }
        return toEventFullDto(eventRepository.save(eventUpdate));
    }

    @Override
    public List<EventShortDto> getAllEvents(Long userId, PageRequest page) {
        checkUser(userId);
        return eventRepository.findAllByInitiator_Id(userId, page)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        checkUser(userId);
        return toEventFullDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с идентификатором: " + eventId + " не найдено")));
    }

    @Override
    @Transactional
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest eventAdminRequest) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getState().equals(PUBLISHED)) {
            throw new IllegalStateException("Событие уже опубликовано");
        }
        final Event eventUpdate = toEventAdminUpdate(event, eventAdminRequest);
        checkEventDatetime(eventUpdate.getEventDate());
        if (eventAdminRequest.getStateAction() != null) {
            if (eventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT) && !event.getState().equals(PENDING)) {
                throw new IllegalStateException("Опубликовать событие можно только если оно PENDING");
            }
            eventUpdate.setState(PUBLISHED);
            eventUpdate.setPublishedOn(LocalDateTime.now());
            if (eventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT)) {
                eventUpdate.setState(REJECT);
            }
        }
        return toEventFullDto(eventRepository.save(eventUpdate));
    }

    @Override
    public List<EventFullDto> getAdminAllEvent(RequestsParamEvent requests) {
        final BooleanExpression conditions = createSearchConditionsForAdmin(requests);
        return eventRepository.findAll(conditions, requests.getPage())
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEvents(RequestsParamEvent requestsParam, HttpServletRequest request, String app, String sort) {
        if (requestsParam.getRangeStart() != null || requestsParam.getRangeEnd() != null)
            if (requestsParam.getRangeEnd().isBefore(requestsParam.getRangeStart())) {
                throw new NumberFormatException("Дата старта должна быть раньше даты окончания");
            }
        statsClient.saveStats(request, app);
        final BooleanExpression conditions = createSearchConditionsForPublic(requestsParam);
        final List<EventShortDto> result = eventRepository.findAll(conditions, requestsParam.getPage())
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        final List<ViewStatsDto> stats = statsClient.getStatsCount(createUris(result), LocalDateTime.now().minusDays(1000), LocalDateTime.now());
        return addStats(result, sort, stats);
    }

    @Override
    public EventFullDto getFullEventById(Long id, HttpServletRequest request, String app) {
        statsClient.saveStats(request, app);
        final EventFullDto event = toEventFullDto(eventRepository.findByIdAndState(id, PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено")));
        final ViewStatsDto stat = statsClient.getStatsCount(List.of(toUrl(event.getId())), LocalDateTime.now().minusDays(1000), LocalDateTime.now())
                .stream()
                .findAny()
                .get();
        event.setViews(stat.getHits());
        return event;
    }

    private User getUserRepository(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с идентификатором : " + userId + " не найден."));
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с идентификатором : " + userId + " не найден.");
        }
    }

    private Category chekCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с идентификатором : " + categoryId + " не найдена."));
    }

    private void checkEventDatetime(LocalDateTime time) {
        if (time.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IllegalStateException("Дата и время на которые намечено событие не может быть раньше,чем за час до времени публикации");
        }
    }

    private String toUrl(Long id) {
        return "/events/" + id;
    }

    private Long toId(String url) {
        String[] uri = url.split("/");
        return Long.valueOf(uri[uri.length - 1]);
    }

    private List<EventShortDto> addStats(List<EventShortDto> result, String sort, List<ViewStatsDto> stats) {
        addViews(result, stats);
        if (sort.equals("VIEWS")) {
            return result
                    .stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
        return result;
    }

    private List<String> createUris(List<EventShortDto> result) {
        return result
                .stream()
                .map(r -> toUrl(r.getId()))
                .collect(Collectors.toList());
    }

    private void addViews(List<EventShortDto> result, List<ViewStatsDto> stats) {
        result
                .forEach(e -> stats.forEach(s -> {
                    if (toId(s.getUri()).equals(e.getId())) {
                        e.setViews(s.getHits());
                    }
                }));
    }

    private BooleanExpression createSearchConditionsForAdmin(RequestsParamEvent requests) {
        final QEvent qEvent = QEvent.event;
        final BooleanExpression conditions;
        if (requests.getUsers() == null) {
            conditions = qEvent.initiator.id.notIn(new ArrayList<>());
        } else {
            conditions = qEvent.initiator.id.in(requests.getUsers());
        }
        if (requests.getCategories() != null) {
            conditions.and(qEvent.category.id.in(requests.getCategories()));
        }
        if (requests.getStates() != null) {
            conditions.and(qEvent.state.in(requests.getStates()));
        }
        if (requests.getRangeStart() != null && requests.getRangeEnd() != null) {
            conditions.and(qEvent.eventDate.between(requests.getRangeStart(), requests.getRangeEnd()));
        }
        return conditions;
    }

    private BooleanExpression createSearchConditionsForPublic(RequestsParamEvent requests) {
        final QEvent qEvent = QEvent.event;
        final BooleanExpression conditions = qEvent.state.eq(PUBLISHED);
        if (requests.getOnlyAvailable()) {
            conditions.and(qEvent.participantLimit.ne(qEvent.confirmedRequests));
        }
        if (requests.getCategories() != null) {
            conditions.and(qEvent.category.id.in(requests.getCategories()));
        }
        if (requests.getText() != null) {
            conditions.and(qEvent.annotation.containsIgnoreCase(requests.getText()));
            conditions.and(qEvent.description.containsIgnoreCase(requests.getText()));
            conditions.and(qEvent.title.containsIgnoreCase(requests.getText()));
        }
        if (requests.getPaid() != null) {
            conditions.and(qEvent.paid.eq(requests.getPaid()));
        }
        if (requests.getRangeStart() != null && requests.getRangeEnd() != null) {
            conditions.and(qEvent.eventDate.between(requests.getRangeStart(), requests.getRangeEnd()));
        } else {
            conditions.and(qEvent.eventDate.after(LocalDateTime.now()));
        }
        return conditions;
    }
}