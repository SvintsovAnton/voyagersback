package group9.events.domain.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "events_activities")
public class EventsActivities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;


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

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventsActivities that = (EventsActivities) o;
        return Objects.equals(id, that.id) && Objects.equals(event, that.event) && Objects.equals(activity, that.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, activity);
    }

    @Override
    public String toString() {
        return "EventsActivities{" +
                "id=" + id +
                ", event=" + event +
                ", activity=" + activity +
                '}';
    }
}
