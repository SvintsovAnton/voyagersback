package group9.events.domain.dto;

import group9.events.domain.entity.Event;
import group9.events.domain.entity.RoleForEvent;
import group9.events.domain.entity.User;

public class EventUserDto {


    private UserDto user;
    private Event event;
    private String nameRoleForEvent;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }



    public String getNameRoleForEvent() {
        return nameRoleForEvent;
    }

    public void setNameRoleForEvent(String nameRoleForEvent) {
        this.nameRoleForEvent = nameRoleForEvent;
    }
}
