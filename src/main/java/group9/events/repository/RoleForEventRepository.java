package group9.events.repository;

import group9.events.domain.entity.Role;
import group9.events.domain.entity.RoleForEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleForEventRepository extends JpaRepository<RoleForEvent, Long> {

    Optional<RoleForEvent> findByTitle(String title);
}
