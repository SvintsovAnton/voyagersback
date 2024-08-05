package group9.events.repository;

import group9.events.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStartDateTimeBefore(LocalDateTime now);

    List<Event> findByStartDateTimeAfter(LocalDateTime now);


}
