package lt.visma.task.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class FilterParameters {
    private final String description;
    private final Integer responsiblePersonId;
    private final Category category;
    private final Type type;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer attendeeCount;
}
