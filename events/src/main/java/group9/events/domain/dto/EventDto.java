package group9.events.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public class EventDto {

    private Long id;

    private String title;

    private String addressStart;

    private LocalDateTime startDateTime;

    private String addressEnd;

    private LocalDateTime endDateTime;

    private BigDecimal cost;

    private Integer maximalNumberOfParticipants;

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

    public void setAddressStart(String addressStart) {
        this.addressStart = addressStart;
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

    public void setAddressEnd(String addressEnd) {
        this.addressEnd = addressEnd;
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
        EventDto eventDto = (EventDto) o;
        return Objects.equals(id, eventDto.id) && Objects.equals(title, eventDto.title) && Objects.equals(addressStart, eventDto.addressStart) && Objects.equals(startDateTime, eventDto.startDateTime) && Objects.equals(addressEnd, eventDto.addressEnd) && Objects.equals(endDateTime, eventDto.endDateTime) && Objects.equals(cost, eventDto.cost) && Objects.equals(maximalNumberOfParticipants, eventDto.maximalNumberOfParticipants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, addressStart, startDateTime, addressEnd, endDateTime, cost, maximalNumberOfParticipants);
    }

    @Override
    public String toString() {
        return "EventDto{" +
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


