package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.Event;
import ru.practicum.util.LifeCycleState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findAllByInitiator_Id(Long userId, PageRequest page);

    List<Event> findAllByCategory_Id(Long catId);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    Optional<Event> findByIdAndState(Long id, LifeCycleState lifeCycleState);
}