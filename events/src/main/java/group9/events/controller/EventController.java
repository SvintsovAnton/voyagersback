package group9.events.controller;

import group9.events.domain.dto.EventCommentsDto;
import group9.events.domain.dto.EventUserDto;
import group9.events.domain.entity.Event;
import group9.events.service.interfaces.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping("/active")
    public List<Event> getActiveEvents() {
        return service.getActiveEvents();
    }

    @GetMapping("/{eventId}")
    public Event getInformationAboutEvent(@PathVariable Long eventId) {
        return service.getInformationAboutEvent(eventId);
    }

    @GetMapping("/archive")
    public List<Event> getArchiveEvents() {
        return service.getArchiveEvents();
    }

    @GetMapping("/{id}/comments")
    public List<EventCommentsDto> seeComments(@PathVariable Long id) {
        return service.seeComments(id);
    }

    @PostMapping("/{eventId}/comments")
    public EventCommentsDto writeComments(@PathVariable Long eventId, @RequestBody EventCommentsDto commentsDto) {
        return service.writeComments(eventId, commentsDto.getComments());
    }

    @GetMapping("/my-points")
    public List<Event> getMyPointsInEvent() {
        return service.getMyPointsInEvent();
    }

    @GetMapping("/my-event")
    public List<Event> getMyEvent() {
        return service.getMyCreatedEvent();
    }

    @PostMapping()
    public Event createEvent(@RequestBody Event event) {
        return service.createEvent(event);
    }

    @DeleteMapping("/{id}")
    public Event removeMyEvent(@PathVariable Long id) {
        return service.removeMyEvent(id);
    }

    @PutMapping("/{id}")
    public Event changeEvent(@PathVariable Long id, @RequestBody Event event) {
        return service.changeEvent(id, event);
    }

    @PostMapping("{eventId}/apply")
    public EventUserDto applyEvent(@PathVariable Long eventId) {
        return service.applyEvent(eventId);
    }

    @DeleteMapping("/{eventId}/cancel")
    public EventUserDto cancelEventRequest(@PathVariable Long eventId) {
        return service.cancelEventRequest(eventId);
    }

}


