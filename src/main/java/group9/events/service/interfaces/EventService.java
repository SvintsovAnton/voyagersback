package group9.events.service.interfaces;

import group9.events.domain.dto.EventCommentsDto;
import group9.events.domain.dto.EventUserDto;
import group9.events.domain.entity.Event;
import group9.events.domain.entity.EventComments;
import group9.events.domain.entity.User;


import java.util.List;

public interface EventService {

    List<Event> getActiveEvents();

    Event getInformationAboutEvent(Long id);

    List<Event> getArchiveEvents();

    List<EventCommentsDto> seeComments(Long eventId);

    EventCommentsDto writeComments(Long eventId, String comments);

    List<Event> getMyPointsInEvent();

    List<Event> getMyCreatedEvent();


    Event createEvent(Event event);

    Event removeMyEvent(Long id);

    Event changeEvent(Long id, Event newEvent);

    EventUserDto applyEvent(Long event_id);

    EventUserDto cancelEventRequest(Long event_id);


}
