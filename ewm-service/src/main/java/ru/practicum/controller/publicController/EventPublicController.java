package ru.practicum.controller.publicController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.RequestsParamEvent;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;
    private static final String APP = "ewm-main-service";

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) @Size(min = 1, max = 7000) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size,
                                         HttpServletRequest request) {
        log.debug("Получен список событий");
        final PageRequest page;
        if (sort.equals("EVENT_DATE")) {
            final Sort sortList = Sort.by("eventDate").descending();
            page = PageRequest.of(from > 0 ? from / size : 0, size, sortList);
        } else {
            page = PageRequest.of(from > 0 ? from / size : 0, size);
        }
        final RequestsParamEvent requestsParamEvent = RequestsParamEvent.builder()
                .text(text)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .page(page)
                .build();
        return eventService.getEvents(requestsParamEvent, request, APP, sort);
    }

    @GetMapping("/{id}")
    public EventFullDto getFullEventById(@PathVariable Long id,
                                         HttpServletRequest request) {
        log.debug("Получена подробная информации об опубликованном событии по идентификатору: {}", id);
        return eventService.getFullEventById(id, request, APP);
    }
}