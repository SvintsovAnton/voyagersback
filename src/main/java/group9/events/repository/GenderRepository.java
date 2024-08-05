package group9.events.repository;

import group9.events.domain.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepository extends JpaRepository<Gender, Long> {
    Gender findByGender(String gender);
}
