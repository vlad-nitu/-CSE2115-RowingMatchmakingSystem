package nl.tudelft.cse.sem.template.user.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
public class BaseActivity {
    private Long activityId;
    private String ownerId;
    private TimeSlot timeSlot;
    private List<String> availablePositions;
    private String certificate;
}
