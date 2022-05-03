package lt.visma.task.repo;

import lt.visma.task.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeetingRepositoryTest {

    private MeetingRepository repo;
    private static final String testData = "testData.json";

    @BeforeEach
    void setUp() {
        repo = new MeetingRepository(testData);
    }

    @AfterEach
    void tearDown() {
        new File(testData).delete();
    }

    @Test
    void addMeeting() {
        assertEquals(0, repo.findMeetings().size());
        repo.addMeeting(new Meeting());
        assertEquals(1, repo.findMeetings().size());
    }

    @Test
    void deleteMeeting() {
        Meeting meeting = new Meeting();
        repo.addMeeting(meeting);
        assertEquals("Deletion failed. Only the responsible person can delete meeting", repo.deleteMeeting(meeting.getResponsiblePersonId()+1,meeting.getId()));
        assertEquals(1,repo.findMeetings().size());
        repo.deleteMeeting(meeting.getResponsiblePersonId(),meeting.getId());
        assertEquals(0,repo.findMeetings().size());
    }

    @Test
    void addAttendee() {
        Meeting meeting1 = new Meeting();
        Meeting meeting2 = new Meeting();
        repo.addMeeting(meeting1);
        repo.addMeeting(meeting2);
        assertEquals(1,repo.findById(meeting1.getId()).getAttendees().size());
        Attendee attendee = new Attendee(1,"Mantas");
        assertEquals("Attendee added", repo.addAttendee(meeting1.getId(), attendee));
        assertEquals(2,repo.findById(meeting1.getId()).getAttendees().size());
        assertEquals("Attendee is already in this meeting", repo.addAttendee(meeting1.getId(), attendee));
        assertEquals(2,repo.findById(meeting1.getId()).getAttendees().size());
        assertEquals("Attendee has another meeting during this time", repo.addAttendee(meeting2.getId(), attendee));
        assertEquals(1,repo.findById(meeting2.getId()).getAttendees().size());
    }

    @Test
    void removeAttendee() {
        Meeting meeting = new Meeting();
        repo.addMeeting(meeting);
        assertEquals("Can't remove responsible person from meeting", repo.removeAttendee(meeting.getId(), meeting.getResponsiblePersonId()));
        assertEquals(1,repo.findById(meeting.getId()).getAttendees().size());
        repo.addAttendee(meeting.getId(),new Attendee(1,"Petras"));
        assertEquals("Attendee removed", repo.removeAttendee(meeting.getId(),1));
        assertEquals(1,repo.findById(meeting.getId()).getAttendees().size());
    }

    @Test
    void findMeetings() {
        assertEquals(0,repo.findMeetings().size());
    }

    @Test
    void findMeetingsFilterDescription() {
        String desc = "java";

        Meeting meeting1 = new Meeting();
        meeting1.setDescription("Jono Java meetas");
        repo.addMeeting(meeting1);

        Meeting meeting2 = new Meeting();
        meeting2.setDescription("Jono .NET meetas");
        repo.addMeeting(meeting2);

        FilterParameters filter = FilterParameters.builder().description(desc).build();
        assertEquals(2, repo.findMeetings().size());
        assertEquals(1, repo.findMeetings(filter).size());
        assertTrue(repo.findMeetings(filter).get(0).getDescription().toLowerCase().contains(desc.toLowerCase()));
    }

    @Test
    void findMeetingsFilterResponsiblePerson() {
        int responsiblePersonId = 1;

        Meeting meeting1 = new Meeting();
        meeting1.setResponsiblePersonId(0);
        repo.addMeeting(meeting1);

        Meeting meeting2 = new Meeting();
        meeting2.setResponsiblePersonId(1);
        repo.addMeeting(meeting2);

        FilterParameters filter = FilterParameters.builder().responsiblePersonId(responsiblePersonId).build();
        assertEquals(2, repo.findMeetings().size());
        assertEquals(1, repo.findMeetings(filter).size());
        assertEquals(responsiblePersonId,repo.findMeetings(filter).get(0).getResponsiblePersonId());
    }


    @Test
    void findMeetingsFilterCategory() {
        Category category = Category.TeamBuilding;

        Meeting meeting1 = new Meeting();
        meeting1.setCategory(category);
        repo.addMeeting(meeting1);

        Meeting meeting2 = new Meeting();
        meeting2.setCategory(Category.CodeMonkey);
        repo.addMeeting(meeting2);

        FilterParameters filter = FilterParameters.builder().category(category).build();
        assertEquals(2, repo.findMeetings().size());
        assertEquals(1, repo.findMeetings(filter).size());
        assertEquals(category,repo.findMeetings(filter).get(0).getCategory());
    }

    @Test
    void findMeetingsFilterType(){
        Type type = Type.Live;

        Meeting meeting1 = new Meeting();
        meeting1.setType(type);
        repo.addMeeting(meeting1);

        Meeting meeting2 = new Meeting();
        meeting2.setType(Type.InPerson);
        repo.addMeeting(meeting2);

        FilterParameters filter = FilterParameters.builder().type(type).build();
        assertEquals(2, repo.findMeetings().size());
        assertEquals(1, repo.findMeetings(filter).size());
        assertEquals(type,repo.findMeetings(filter).get(0).getType());
    }

    @Test
    void findMeetingsFilterDates(){
        LocalDateTime startDate1 = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endDate1 = LocalDateTime.now().plusMinutes(90).truncatedTo(ChronoUnit.SECONDS);
        Meeting meeting1 = new Meeting();
        meeting1.setStartDate(startDate1);
        meeting1.setEndDate(endDate1);
        repo.addMeeting(meeting1);

        LocalDateTime startDate2 = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endDate2 = LocalDateTime.now().plusDays(1).plusMinutes(45).truncatedTo(ChronoUnit.SECONDS);
        Meeting meeting2 = new Meeting();
        meeting2.setStartDate(startDate2);
        meeting2.setEndDate(endDate2);
        repo.addMeeting(meeting2);
        // Meeting from startDate2
        FilterParameters filter = FilterParameters.builder().startDate(startDate2).build();
        assertEquals(2, repo.findMeetings().size());
        assertEquals(1, repo.findMeetings(filter).size());
        assertEquals(startDate2,repo.findMeetings(filter).get(0).getStartDate());
        // Meetings until endDate1
        filter = FilterParameters.builder().endDate(endDate1).build();
        assertEquals(1, repo.findMeetings(filter).size());
        assertEquals(endDate1,repo.findMeetings(filter).get(0).getEndDate());

        LocalDateTime startDate3 = LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endDate3 = LocalDateTime.now().plusDays(5).plusMinutes(45).truncatedTo(ChronoUnit.SECONDS);
        Meeting meeting3 = new Meeting();
        meeting3.setStartDate(startDate3);
        meeting3.setEndDate(endDate3);
        repo.addMeeting(meeting3);
        // Meetings in between startDate1 and endDate2
        filter = FilterParameters.builder().startDate(startDate1).endDate(endDate2).build();
        assertEquals(2, repo.findMeetings(filter).size());
        assertEquals(startDate1, repo.findMeetings(filter).get(0).getStartDate());
        assertEquals(startDate2, repo.findMeetings(filter).get(1).getStartDate());
    }

    @Test
    void findMeetingsFilterAttendeeCount(){
        int attendeeCount = 2;

        Meeting meeting1 = new Meeting();
        repo.addMeeting(meeting1);

        Meeting meeting2 = new Meeting();
        List<Attendee> attendeeList = new ArrayList<>();
        attendeeList.add(new Attendee());
        attendeeList.add(new Attendee());
        meeting2.setAttendees(attendeeList);
        repo.addMeeting(meeting2);

        FilterParameters filter = FilterParameters.builder().attendeeCount(attendeeCount).build();
        assertEquals(2, repo.findMeetings().size());
        assertEquals(1, repo.findMeetings(filter).size());
        assertEquals(attendeeCount,repo.findMeetings(filter).get(0).getAttendees().size());
    }

    @Test
    void saveData() {
        repo.addMeeting(new Meeting(1, "Meeting", 0, "Jono Java meetas", Category.TeamBuilding,
                Type.Live, LocalDateTime.parse("2022-01-01T15:31:17"), LocalDateTime.parse("2022-01-01T15:41:17"),
                new ArrayList<>(List.of(new Attendee(1,"Jonas",LocalDateTime.parse("2022-01-01T15:31:17"))))));
        try{
            String testFileData = Files.readString(Path.of(testData));
            assertEquals("[{\"id\":1,\"name\":\"Meeting\",\"responsiblePersonId\":0,\"description\":\"Jono Java meetas\",\"category\":\"TeamBuilding\"," +
                    "\"type\":\"Live\",\"startDate\":\"2022-01-01T15:31:17\",\"endDate\":\"2022-01-01T15:41:17\",\"attendees\":[{\"id\":1,\"name\":\"Jonas\"," +
                    "\"timeAdded\":\"2022-01-01T15:31:17\"}]}]",testFileData);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    void loadData() {
        repo.addMeeting(new Meeting(1, "Meeting", 0, "Jono Java meetas", Category.TeamBuilding,
                Type.Live, LocalDateTime.parse("2022-01-01T15:31:17"), LocalDateTime.parse("2022-01-01T15:41:17"),
                new ArrayList<>(List.of(new Attendee(1,"Jonas",LocalDateTime.parse("2022-01-01T15:31:17"))))));
        repo = new MeetingRepository(testData);
        assertEquals("[Meeting{id=1, name='Meeting', responsiblePersonId=0, description='Jono Java meetas', category=TeamBuilding," +
                " type=Live, startDate=2022-01-01T15:31:17, endDate=2022-01-01T15:41:17, attendees=[Attendee{id=1, name='Jonas'," +
                " timeAdded=2022-01-01T15:31:17}]}]",repo.findMeetings().toString());
    }

}