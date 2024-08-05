package group9.events.service.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import group9.events.domain.dto.EventUserDto;
import group9.events.domain.entity.EventUsers;
import group9.events.exception_handler.exceptions.ResourceNotFoundException;
import group9.events.repository.RoleForEventRepository;
import group9.events.repository.UserRepository;


@Service
public class EventUserMappingService {
    private final UserMappingService userMappingService;
    private final UserRepository userRepository;
    private final RoleForEventRepository roleForEventRepository;

    @Autowired
    public EventUserMappingService(UserMappingService userMappingService, UserRepository userRepository, RoleForEventRepository roleForEventRepository) {
        this.userMappingService = userMappingService;
        this.userRepository = userRepository;
        this.roleForEventRepository = roleForEventRepository;
    }


   public   EventUserDto mapEntityToDto(EventUsers eventUsers){
         EventUserDto eventUserDto = new EventUserDto();
         eventUserDto.setUser(userMappingService.mapEntityToDto(eventUsers.getUser()));
         eventUserDto.setEvent(eventUsers.getEvent());
         String roleTitle = eventUsers.getRoleForEvent().getTitle();
         String nameRoleForEvent = roleTitle.startsWith("ROLE_") ? roleTitle.substring(5) : roleTitle;
         eventUserDto.setNameRoleForEvent(nameRoleForEvent);
        return eventUserDto;
     }




    public  EventUsers mapDtoToEntity(EventUserDto eventUserDto){
         EventUsers eventUsers = new EventUsers();
        eventUsers.setUser(userRepository.findByEmail(eventUserDto.getUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
         eventUsers.setEvent(eventUserDto.getEvent());
         String nameRoleForEvent = eventUserDto.getNameRoleForEvent();
         String role ="ROLE_";
        String roleTitle = nameRoleForEvent.startsWith(role) ? nameRoleForEvent : role + nameRoleForEvent;
        eventUsers.setRoleForEvent(roleForEventRepository.findByTitle(roleTitle)
                .orElseThrow(() -> new ResourceNotFoundException("Role for event not found")));
         return eventUsers;
             }
}
