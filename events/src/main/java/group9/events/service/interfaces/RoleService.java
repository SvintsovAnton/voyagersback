package group9.events.service.interfaces;

import group9.events.domain.entity.Role;
import org.springframework.stereotype.Service;


public interface RoleService {

    Role getRoleUser();

    Role getRoleAdmin();
}
