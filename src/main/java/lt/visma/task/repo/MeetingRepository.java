package lt.visma.task.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lt.visma.task.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@PropertySource("/application.properties")
@Service
public class MeetingRepository{

    private static List<Meeting> meetingList;
    @Value("${data.file}")
    private String dataFile;

    public MeetingRepository() {
        meetingList = new ArrayList<>();
    }

    public MeetingRepository(String dataPath){
        meetingList = new ArrayList<>();
        if(dataPath != null){
            dataFile = dataPath;
        }
    }

    public void loadData(){
        File file = new File(dataFile);
        if(!file.exists() || file.isDirectory() ){
            System.out.println("File not found");
            return;
        }
        else if(file.length() == 0){
            System.out.println("File is empty");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        TypeReference<List<Meeting>> typeReference = new TypeReference<>() {};
        try{
            meetingList = mapper.readValue(file,typeReference);
        }catch (IOException e){
            System.out.println("Unable to load meetings: " + e.getMessage());
        }
    }

    public void saveData(){
        File file = new File(dataFile);
        if(file.isDirectory() ){
            System.out.println("File is a directory");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        try{
            mapper.writeValue(file,meetingList);
        }catch (IOException e){
            System.out.println("Unable to save meetings: " + e.getMessage());
        }
    }
    public Meeting findById(int id){
        loadData();
        for (Meeting meeting : meetingList) {
            if (meeting.getId()==id) {
                return meeting;
            }
        }
        return null;
    }

    public List<Meeting> findMeetings(FilterParameters filters) {
        loadData();
        String description = filters.getDescription();
        Integer responsibleId = filters.getResponsiblePersonId();
        Category category = filters.getCategory();
        Type type = filters.getType();
        LocalDateTime startDate = filters.getStartDate();
        LocalDateTime endDate = filters.getEndDate();
        Integer attendeeCount = filters.getAttendeeCount();

        return meetingList.stream().filter(meeting ->
                (description == null || description.isBlank() || Pattern.compile(description, Pattern.CASE_INSENSITIVE).matcher(meeting.getDescription()).find())
                        && (responsibleId == null || meeting.getResponsiblePersonId() == responsibleId)
                        && (category == null || meeting.getCategory().equals(category))
                        && (type == null || meeting.getType().equals(type))
                        && (startDate == null || meeting.getStartDate().compareTo(startDate) >= 0)
                        && (endDate == null || meeting.getStartDate().compareTo(endDate) < 0)
                        && (attendeeCount == null || meeting.getAttendees().size() >= attendeeCount)
        ).collect(Collectors.toList());
    }

    public List<Meeting> findMeetings(){
        loadData();
        return meetingList;
    }

    public void addMeeting(Meeting meeting) {
        meetingList.add(meeting);
        saveData();
    }

    public String deleteMeeting(int userId, int meetingId) {
        Meeting meeting = findById(meetingId);
        if(meeting != null && meeting.getResponsiblePersonId()==userId){
            meetingList.remove(meeting);
            saveData();
            return "Deleted";
        }
        if(meeting == null){
            return "Deletion failed. Meeting not found";
        }

        return "Deletion failed. Only the responsible person can delete meeting";
    }

    public String addAttendee(int meetingId, Attendee attendee) {
        Meeting meeting = findById(meetingId);
        if(meeting == null){
            return "Meeting not found";
        }
        for (Attendee att : meeting.getAttendees()) {
            if (attendee.getId()==att.getId()) {
                return "Attendee is already in this meeting";
            }
        }
        for(Meeting otherMeeting : meetingList){
            if(meeting != otherMeeting){
                for (Attendee att : otherMeeting.getAttendees()) {
                    if (attendee.getId() == att.getId()) {
                        if(meeting.getStartDate().isBefore(otherMeeting.getEndDate()) && otherMeeting.getStartDate().isBefore(meeting.getEndDate())){
                            return "Attendee has another meeting during this time";
                        }
                    }
                }
            }
        }
        List<Attendee> newList = meeting.getAttendees();
        newList.add(attendee);
        meeting.setAttendees(newList);
        int index = meetingList.indexOf(meeting);
        meetingList.set(index, meeting);
        saveData();
        return "Attendee added";
    }

    public String removeAttendee(int meetingId, int userId) {
        Meeting meeting = findById(meetingId);
        if(meeting == null){
            return "Meeting not found";
        }
        if(meeting.getResponsiblePersonId()==userId){
            return "Can't remove responsible person from meeting";
        }
        for (Attendee att : meeting.getAttendees()) {
            if (userId==att.getId()) {
                List<Attendee> newList = meeting.getAttendees();
                newList.remove(att);
                meeting.setAttendees(newList);
                int index = meetingList.indexOf(meeting);
                meetingList.set(index, meeting);
                saveData();
                return "Attendee removed";
            }
        }
        return "Attendee not found in meeting";
    }
}
