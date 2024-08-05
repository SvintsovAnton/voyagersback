package group9.events.service;

import group9.events.domain.entity.Role;
import group9.events.repository.RoleRepository;
import group9.events.service.interfaces.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {


    private final RoleRepository repository;

    public RoleServiceImpl(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Role getRoleUser() {
        return repository.findByTitle("ROLE_USER").orElseThrow(
                () -> new RuntimeException("Database doesn´t content ROLE_USER"));
    }

    @Override
    public Role getRoleAdmin() {
        return repository.findByTitle("ROLE_ADMIN").orElseThrow(
                () -> new RuntimeException("Database doesn´t content ROLE_ADMIN"));
    }
}
