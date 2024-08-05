package group9.events.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "event_comments")
public class EventComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @NotNull(message = "Event cannot be null")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;

    @Column(name = "date_time", nullable = false)
    @NotNull(message = "Date and time cannot be null")
    private LocalDateTime dateTime;

    @Column(name = "comments", nullable = false)
    @NotBlank(message = "Comment cannot be blank")
    private String comments;

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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventComments that = (EventComments) o;
        return Objects.equals(id, that.id) && Objects.equals(event, that.event) && Objects.equals(user, that.user) && Objects.equals(dateTime, that.dateTime) && Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, user, dateTime, comments);
    }

    @Override
    public String toString() {
        return "EventComments{" +
                "id=" + id +
                ", event=" + event +
                ", user=" + user +
                ", dateTime=" + dateTime +
                ", comments='" + comments + '\'' +
                '}';
    }
}