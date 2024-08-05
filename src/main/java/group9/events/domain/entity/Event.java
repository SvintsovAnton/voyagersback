package group9.events.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    @NotNull(message = "Event title cannot be null")
    @NotBlank(message = "Event title cannot be empty")
    @Pattern(
            regexp = "[A-Z][a-z ]{3,}",
            message = "Event title should be at least 3 character length " +
                    "and start with capital letter."
    )
    private String title;

    @Column(name = "address_start")
    private String addressStart;

    @Column(name = "start_datetime")
    private LocalDateTime startDateTime;

    @Column(name = "address_end")
    private String addressEnd;

    @Column(name = "end_datetime")
    private LocalDateTime endDateTime;

    @Column(name = "cost")
    private BigDecimal cost;

    @JsonProperty("maximal_number_of_participants")
    @Column(name = "maximal_number_of_participants")
    private Integer maximalNumberOfParticipants;


    @Column(name = "active", nullable = false)
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddressStart() {
        return addressStart;
    }

    public void setAddressStart(String adressStart) {
        this.addressStart = adressStart;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getAddressEnd() {
        return addressEnd;
    }

    public void setAddressEnd(String adressEnd) {
        this.addressEnd = adressEnd;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Integer getMaximalNumberOfParticipants() {
        return maximalNumberOfParticipants;
    }

    public void setMaximalNumberOfParticipants(Integer maximalNumberOfParticipants) {
        this.maximalNumberOfParticipants = maximalNumberOfParticipants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return active == event.active && Objects.equals(id, event.id) && Objects.equals(title, event.title) && Objects.equals(addressStart, event.addressStart) && Objects.equals(startDateTime, event.startDateTime) && Objects.equals(addressEnd, event.addressEnd) && Objects.equals(endDateTime, event.endDateTime) && Objects.equals(cost, event.cost) && Objects.equals(maximalNumberOfParticipants, event.maximalNumberOfParticipants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, addressStart, startDateTime, addressEnd, endDateTime, cost, maximalNumberOfParticipants);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", addressStart='" + addressStart + '\'' +
                ", startDateTime=" + startDateTime +
                ", addressEnd='" + addressEnd + '\'' +
                ", endDateTime=" + endDateTime +
                ", cost=" + cost +
                ", maximalNumberOfParticipants=" + maximalNumberOfParticipants +
                '}';
    }
}
