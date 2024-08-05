package group9.events.domain.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "event_users")
public class EventUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "roles_for_events_id", nullable = false)
    private RoleForEvent roleForEvent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoleForEvent getRoleForEvent() {
        return roleForEvent;
    }

    public void setRoleForEvent(RoleForEvent roleForEvent) {
        this.roleForEvent = roleForEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventUsers that = (EventUsers) o;
        return Objects.equals(id, that.id) && Objects.equals(event, that.event) && Objects.equals(user, that.user) && Objects.equals(roleForEvent, that.roleForEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, user, roleForEvent);
    }

    @Override
    public String toString() {
        return "EventUsers{" +
                "id=" + id +
                ", event=" + event +
                ", user=" + user +
                '}';
    }
}