package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.util.StatusRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequester_Id(Long userId);

    List<ParticipationRequest> findAllByEvent_Id(Long eventId);

    List<ParticipationRequest> findAllByEvent_IdAndIdNotInAndStatus(Long id, List<Long> listId, StatusRequest status);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);
}