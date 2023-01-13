package nl.tudelft.cse.sem.template.user.utils;

import lombok.*;

import java.util.List;
import java.util.Set;

/**
 * The type Base activity.
 */
@NoArgsConstructor
public class BaseActivity {
    private Long activityId;
    @Getter
    @Setter private String ownerId;
    private TimeSlot timeSlot;
    private List<String> positions;
    private String certificate;
    private boolean isCompetitive;
    private char gender;
    private String organisation;
    @Setter  private String type; // "activity" or "competition"

    /**
     * All args constructor.
     *
     * @param activityId - Long activityId
     * @param ownerId - String ownerId
     * @param timeSlot - timeSlot
     * @param positions - Set String positions
     * @param certificate - String certificate
     * @param isCompetitive - Boolean value
     * @param gender - character
     * @param organisation - String organisation
     */
    public BaseActivity(Long activityId, String ownerId,
                        TimeSlot timeSlot,
                        List<String> positions,
                        String certificate,
                        boolean isCompetitive,
                        char gender,
                        String organisation) {
        this.activityId = activityId;
        this.ownerId = ownerId;
        this.timeSlot = timeSlot;
        this.positions = positions;
        this.certificate = certificate;
        this.isCompetitive = isCompetitive;
        this.gender = gender;
        this.organisation = organisation;
    }


}
