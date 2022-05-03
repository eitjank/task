package lt.visma.task.controller;

import lt.visma.task.model.*;
import lt.visma.task.repo.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class MeetingController
{
    @Autowired
    private MeetingRepository repo;

    @GetMapping("/meetings")
    public List<Meeting> getMeetings(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer responsiblePersonId,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer attendeeCount
    ){
        LocalDateTime stDate = (startDate==null) ? null : LocalDateTime.parse(startDate);
        LocalDateTime enDate = (endDate==null) ? null : LocalDateTime.parse(endDate);
        FilterParameters filters = FilterParameters.builder().description(description).responsiblePersonId(responsiblePersonId).
                        category(category).type(type).startDate(stDate).endDate(enDate).attendeeCount(attendeeCount).build();
        return repo.findMeetings(filters);
    }

    @PostMapping("/meetings")
    public Meeting addMeeting(@RequestBody Meeting meeting){
        repo.addMeeting(meeting);
        return meeting;
    }

    @DeleteMapping("/{userId}/meetings/{meetingId}")
    public String deleteMeeting(@PathVariable("userId") int userId, @PathVariable("meetingId") int meetingId){
        return repo.deleteMeeting(userId, meetingId);
    }

    @PostMapping("/meetings/{meetingId}")
    public String addAttendee(@PathVariable("meetingId")int meetingId, @RequestBody Attendee attendee){
        return repo.addAttendee(meetingId, attendee);
    }

    @DeleteMapping("/meetings/{meetingId}/{userId}")
    public String removeAttendee(@PathVariable("meetingId")int meetingId, @PathVariable("userId")int userId){
        return repo.removeAttendee(meetingId,userId);
    }
}
