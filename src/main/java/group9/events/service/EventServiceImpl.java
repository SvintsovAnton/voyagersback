package group9.events.service;

import group9.events.domain.dto.EventCommentsDto;
import group9.events.domain.dto.EventUserDto;
import group9.events.domain.entity.*;
import group9.events.exception_handler.exceptions.*;
import group9.events.repository.*;
import group9.events.service.interfaces.EventService;
import group9.events.service.mapping.EventCommentsMappingService;
import group9.events.service.mapping.EventUserMappingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventCommentsRepository eventCommentsRepository;
    private final UserRepository userRepository;
    private final EventUsersRepository eventUsersRepository;
    private final EventCommentsMappingService eventCommentsMappingService;
    private final RoleForEventRepository roleForEventRepository;
    private final EventsActivitiesRepository eventsActivitiesRepository;
    private final EventUserMappingService eventUserMappingService;

    public EventServiceImpl(EventRepository eventRepository, EventCommentsRepository eventCommentsRepository, UserRepository userRepository, EventUsersRepository eventUsersRepository, EventCommentsMappingService eventCommentsMappingService, RoleForEventRepository roleForEventRepository, EventsActivitiesRepository eventsActivitiesRepository, EventUserMappingService eventUserMappingService) {
        this.eventRepository = eventRepository;
        this.eventCommentsRepository = eventCommentsRepository;
        this.userRepository = userRepository;
        this.eventUsersRepository = eventUsersRepository;
        this.eventCommentsMappingService = eventCommentsMappingService;
        this.roleForEventRepository = roleForEventRepository;
        this.eventsActivitiesRepository = eventsActivitiesRepository;
        this.eventUserMappingService = eventUserMappingService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String userName = authentication.getName();
            User user = userRepository.findByEmail(userName).orElse(null);
            return user;
        }
        throw new UserNotAuthenticatedException("User is not authenticated");
    }

    @Override
    public List<Event> getActiveEvents() {
        List<Event> listOfEvents = eventRepository.findByStartDateTimeAfter(LocalDateTime.now());
        if (!listOfEvents.isEmpty()) {
            return listOfEvents;
        }
        throw new ResourceNotFoundException("No active events found");

    }


    @Override
    public Event getInformationAboutEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return event;
    }

    @Override
    public List<Event> getArchiveEvents() {
        List<Event> listOfArchiveEvents = eventRepository.findByStartDateTimeBefore(LocalDateTime.now());
        if (!listOfArchiveEvents.isEmpty()) {
            return listOfArchiveEvents;
        }
        throw new ResourceNotFoundException("No archive events found");
    }

    @Override
    public List<EventCommentsDto> seeComments(Long eventId) {
        List<EventCommentsDto> listOfEventComments = eventCommentsRepository.findByEventId(eventId).stream().
                map(eventCommentsMappingService::mapEntityToDto)
                .collect(Collectors.toList());
        if (listOfEventComments.isEmpty()) {
            throw new ResourceNotFoundException("There are no comments yet");
        }
        return listOfEventComments;
    }


    @Override
    public EventCommentsDto writeComments(Long eventId, String comments) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User user = getCurrentUser();

        if (event.getEndDateTime().isAfter(LocalDateTime.now())) {
            throw new EventNotEndedException("Event has not ended yet");
        }
        if (comments.isEmpty()) {
            throw new EmptyCommentException("comment cannot be empty");
        }

        EventComments eventComments = new EventComments();
        eventComments.setEvent(event);
        eventComments.setUser(user);
        eventComments.setComments(comments);
        eventComments.setDateTime(LocalDateTime.now());
        eventCommentsRepository.save(eventComments);
        return eventCommentsMappingService.mapEntityToDto(eventComments);

    }



    @Override
    public List<Event> getMyPointsInEvent() {
        User user = getCurrentUser();
        RoleForEvent roleForEvent;
       roleForEvent = roleForEventRepository.findByTitle("ROLE_PARTICIPANT").orElseThrow(()->new ResourceNotFoundException("Role don´t found"));
       List<EventUsers> listOfUsers = eventUsersRepository.findEventUsersByUser_IdAndRoleForEvent(user.getId(),roleForEvent).orElseThrow(()->new ResourceNotFoundException("Events for this user don´t found"));
       List<Event> eventList =listOfUsers.stream().map(x->x.getEvent()).toList();
       return eventList;
    }

    @Override
    public List<Event> getMyCreatedEvent() {
        User user = getCurrentUser();
        RoleForEvent roleForEvent;
        roleForEvent = roleForEventRepository.findByTitle("ROLE_OWNER").orElseThrow(()->new ResourceNotFoundException("Role don´t found"));
        List<EventUsers> listOfUsers = eventUsersRepository.findEventUsersByUser_IdAndRoleForEvent(user.getId(),roleForEvent).orElseThrow(()->new ResourceNotFoundException("Events for this user don´t found"));
        List<Event> eventList =listOfUsers.stream().map(x->x.getEvent()).toList();
        return eventList;
    }


    @Override
    public Event createEvent(Event event) {
        event.setId(null);
        if (event == null) {
            throw new ResourceNotFoundException("Event not created");
        }
        eventRepository.save(event);
        EventUsers eventUsers = new EventUsers();
        eventUsers.setEvent(event);
        User user = getCurrentUser();
        eventUsers.setUser(user);
        eventUsers.setUser(getCurrentUser());
        eventUsers.setRoleForEvent(roleForEventRepository.findByTitle("ROLE_OWNER").orElse(null));
        eventUsersRepository.save(eventUsers);
        return event;
    }

    //TODO  Exception when a user wants to delete an application that he is not the owner of
    @Override
    public Event removeMyEvent(Long id) {
        User user = getCurrentUser();
        eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        List<EventUsers> listOfEventUsers = eventUsersRepository.findEventUsersByUser_Id(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        EventUsers eventUsers = listOfEventUsers.stream()
                .filter(x -> x.getEvent().
                        getId().equals(id) && x.
                        getRoleForEvent().getTitle().equals("ROLE_OWNER"))
                .findFirst().orElse(null);
        if (eventUsers != null) {
            List<EventUsers> allEventUsersInEvent = eventUsersRepository.findEventUsersByEvent_Id(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
            eventUsersRepository.deleteAll(allEventUsersInEvent);

            List<EventComments> allCommentsInEvent = eventCommentsRepository.findByEventId(id);
            if (!allCommentsInEvent.isEmpty()) {
                eventCommentsRepository.deleteAll(allCommentsInEvent);
            }
            List<EventsActivities> allActivitesInEvent = eventsActivitiesRepository.findByEventId(id);
            if (!allActivitesInEvent.isEmpty()) {
                eventsActivitiesRepository.deleteAll(allActivitesInEvent);
            }

            Event event = getInformationAboutEvent(id);
            eventRepository.deleteById(id);
            return event;
        }
        throw new AccessDeniedException("You can't delete events that don't belong to you");

    }

    //TODO An exception is when a user wants to change an application that he is not the owner of.
    @Override
    public Event changeEvent(Long id, Event newEvent) {
        User user = getCurrentUser();
        eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        List<EventUsers> listOfEventUsers = eventUsersRepository.findEventUsersByUser_Id(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        EventUsers eventUsers = listOfEventUsers.stream()
                .filter(x -> x.getEvent().
                        getId().equals(id) && x.
                        getRoleForEvent().getTitle().equals("ROLE_OWNER"))
                .findFirst().orElse(null);

        if (eventUsers != null) {
            Event altEvent = eventRepository.findById(id).orElse(null);
            if (altEvent != null) {
                altEvent.setTitle(newEvent.getTitle());
                altEvent.setAddressStart(newEvent.getAddressStart());
                altEvent.setStartDateTime(newEvent.getStartDateTime());
                altEvent.setAddressEnd(newEvent.getAddressEnd());
                altEvent.setEndDateTime(newEvent.getEndDateTime());
                altEvent.setCost(newEvent.getCost());
                altEvent.setMaximalNumberOfParticipants(newEvent.getMaximalNumberOfParticipants());
                return eventRepository.save(altEvent);
            }
        }
        throw new AccessDeniedException("You can't change events that don't belong to you");
    }


    @Override
    public EventUserDto applyEvent(Long eventId) {
        User user = getCurrentUser();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        List<EventUsers> oldUserEvents = eventUsersRepository.findAllByEvent_IdAndUser_Id(eventId, user.getId()).orElse(null);
        if (!oldUserEvents.isEmpty()) {
            throw new AlreadyRegisteredForEventException("You are already registered for this event.");
        }

        long participantCount = eventUsersRepository.findEventUsersByEvent_Id(eventId).stream().count();
        if (participantCount >= event.getMaximalNumberOfParticipants()) {
            throw new MaxParticipantsExceededException("The number of event participants has been exceeded");
        }
        EventUsers eventUsers = new EventUsers();
        eventUsers.setEvent(event);
        eventUsers.setUser(user);
        eventUsers.setRoleForEvent(roleForEventRepository.findByTitle("ROLE_PARTICIPANT").orElseThrow(() -> new ResourceNotFoundException("Role not found")));
        eventUsersRepository.save(eventUsers);
        return  eventUserMappingService.mapEntityToDto(eventUsers);

    }


    @Override
    public EventUserDto cancelEventRequest(Long eventId) {
        User user = getCurrentUser();
        EventUsers eventUsers = eventUsersRepository.findByEvent_IdAndUser_Id(eventId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("You were not registered for this event"));
        if (eventUsers.getRoleForEvent().getTitle().toString().equals(roleForEventRepository.findByTitle("ROLE_OWNER").get().getTitle().toString())) {
            throw new OwnerCannotCancelParticipationException("You cannot cancel participation in an event that you own. You need to delete the events");
        }
        eventUsersRepository.delete(eventUsers);
        return  eventUserMappingService.mapEntityToDto(eventUsers);
    }
}