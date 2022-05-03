package lt.visma.task.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Attendee {
    private int id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeAdded;

    public Attendee(int id, String name, LocalDateTime timeAdded) {
        this.id = id;
        this.name = name;
        this.timeAdded = timeAdded;
    }

    public Attendee(int id, String name) {
        this.id = id;
        this.name = name;
        this.timeAdded = LocalDateTime.now();
    }

    public Attendee() {
        this(0, "Jonas", LocalDateTime.now());
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

    public LocalDateTime getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(LocalDateTime timeAdded) {
        this.timeAdded = timeAdded;
    }

    @Override
    public String toString() {
        return "Attendee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timeAdded=" + timeAdded +
                '}';
    }
}
