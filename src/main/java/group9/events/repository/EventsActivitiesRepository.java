package group9.events.repository;

import group9.events.domain.entity.EventComments;
import group9.events.domain.entity.EventsActivities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventsActivitiesRepository extends JpaRepository<EventsActivities, Long> {

    List<EventsActivities> findByEventId(Long eventId);
}
