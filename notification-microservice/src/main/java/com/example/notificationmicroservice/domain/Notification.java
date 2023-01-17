package com.example.notificationmicroservice.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "targetId", nullable = false)
    @NotBlank(message = "targetId is mandatory and cannot be blank")
    private String targetId;

    @Column(name = "activityId", nullable = false)
    @NotNull(message = "activityId is mandatory and cannot be null")
    private Long activityId;

    @Column(name = "type", nullable = false)
    @NotBlank(message = "type is mandatory and cannot be blank")
    private String type;

    @Column(name = "position")
    @NotBlank(message = "position is mandatory and cannot be blank")
    private String position;

    /** This Notification constructor is needed because I can't use.
     * All args constructor since id is assigned by the db.
     *
     * @param targetId to user
     * @param activityId regarding activity
     * @param type of type
     * @param position for position if applicable
     */
    public Notification(String targetId, Long activityId, String type, String position) {
        this.targetId = targetId;
        this.activityId = activityId;
        this.type = type;
        this.position = position;
    }

    /** The message that is to be sent to the user based on notification type and position.
     *
     * @return the built message
     */
    public String buildMessage() {
        if (type.equals("notifyUser")) {
            return "Congratulations, you have been accepted as a " + position
                    + " for activity with Id: " + activityId;
        } else  {
            return "A new user has applied as a " + position
                    + " for activity with Id: " + activityId;
        }
    }

    /** Position getter.
     *
     * @return position
     */
    public String getPosition() {
        return position;
    }

    /** Type getter.
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /** ActivityId getter. Even though it says it is unused
     * the @Valid annotation needs it to work properly
     *
     * @return activityId
     */
    public Long getActivityId() {
        return activityId;
    }

    /** TargetId getter.
     *
     * @return targetId
     */
    public String getTargetId() {
        return targetId;
    }
}