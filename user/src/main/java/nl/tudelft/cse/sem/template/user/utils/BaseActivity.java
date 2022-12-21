package nl.tudelft.cse.sem.template.user.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import java.util.List;
import java.util.Set;

/**
 * The type Base activity.
 */
@AllArgsConstructor
@EqualsAndHashCode
public class BaseActivity {
    private Long activityId;
    private String ownerId;
    private TimeSlot timeSlot;
    private Set<String> positions;
    private String certificate;
    private String type;

    /**
     * Gets positions.
     *
     * @return the positions
     */
    public Set<String> getPositions() {
        return positions;
    }

    /**
     * Sets positions.
     *
     * @param positions the positions
     */
    public void setPositions(Set<String> positions) {
        this.positions = positions;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets activity id.
     *
     * @return the activity id
     */
    public Long getActivityId() {
        return activityId;
    }

    /**
     * Sets activity id.
     *
     * @param activityId the activity id
     */
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    /**
     * Gets owner id.
     *
     * @return the owner id
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Sets owner id.
     *
     * @param ownerId the owner id
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Gets time slot.
     *
     * @return the time slot
     */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * Sets time slot.
     *
     * @param timeSlot the time slot
     */
    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }


    /**
     * Gets certificate.
     *
     * @return the certificate
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Sets certificate.
     *
     * @param certificate the certificate
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

}
