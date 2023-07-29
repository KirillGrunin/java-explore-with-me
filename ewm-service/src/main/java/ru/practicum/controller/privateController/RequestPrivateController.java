package ru.practicum.controller.privateController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestPrivateController {
    private final ParticipationRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveParticipationRequest(@PathVariable Long userId,
                                                            @RequestParam Long eventId) {
        log.debug("Добавлен запрос на участие в событии с идентификатором: {}", eventId);
        return requestService.saveParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId,
                                                              @PathVariable Long requestId) {
        log.debug("Запрос на участие отменен");
        return requestService.cancelParticipationRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllParticipationRequest(@PathVariable Long userId) {
        log.debug("Получен список заявок на участие для пользователя:{}", userId);
        return requestService.getAllParticipationRequest(userId);
    }
}