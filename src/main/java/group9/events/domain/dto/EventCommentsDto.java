package group9.events.domain.dto;

public class EventCommentsDto {
    private String firstName;
    private String lastName;
    private String comments;
    private String eventTitle;

    public EventCommentsDto() {
    }

    public EventCommentsDto(String firstName, String lastName, String comments, String eventTitle) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.comments = comments;
        this.eventTitle = eventTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    @Override
    public String toString() {
        return "EventCommentDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", comments='" + comments + '\'' +
                ", eventTitle='" + eventTitle + '\'' +
                '}';
    }
}