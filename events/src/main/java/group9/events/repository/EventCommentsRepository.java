package group9.events.repository;

import group9.events.domain.entity.EventComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventCommentsRepository extends JpaRepository<EventComments, Long> {

    List<EventComments> findByEventId(Long eventId);
}
