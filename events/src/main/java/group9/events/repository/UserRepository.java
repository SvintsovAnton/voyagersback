package group9.events.repository;

import group9.events.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBylastName(String lastname);

    Optional<User> findBydateOfBirth(Date dateOfBirth);

    Optional<User> findByEmail(String email);

    Optional<User> findFirstByLastName(String lastName);
}
