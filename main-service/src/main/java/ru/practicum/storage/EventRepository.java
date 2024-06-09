package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.entity.Event;
import ru.practicum.enums.EventStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findAllByIdIn(Set<Long> events);

    List<Event> findEventByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findEventByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndState(Long id, EventStatus state);
}
