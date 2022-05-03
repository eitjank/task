package lt.visma.task.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Meeting {

    private int id;
    private static int meetingCount=0;
    private String name;
    private int responsiblePersonId;
    private String description;
    private Category category;
    private Type type;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    private List<Attendee> attendees;

    public Meeting(int id, String name, int responsiblePersonId, String description, Category category, Type type, LocalDateTime startDate, LocalDateTime endDate, List<Attendee> attendees) {
        this.id = id;
        this.name = name;
        this.responsiblePersonId = responsiblePersonId;
        this.description = description;
        this.category = category;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.attendees = attendees;
        meetingCount++;
    }

    public Meeting() {
        this.id = meetingCount;
        this.name = "Meeting";
        this.responsiblePersonId = 0;
        this.description = "Jono Java meetas";
        this.category = Category.Hub;
        this.type = Type.Live;
        this.startDate = LocalDateTime.now();
        this.endDate = LocalDateTime.now().plusMinutes(45);
        this.attendees = new ArrayList<>(List.of(new Attendee()));
        meetingCount++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResponsiblePersonId() {
        return responsiblePersonId;
    }

    public void setResponsiblePersonId(int responsiblePersonId) {
        this.responsiblePersonId = responsiblePersonId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<Attendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", responsiblePersonId=" + responsiblePersonId +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", type=" + type +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", attendees=" + attendees +
                '}';
    }
}
