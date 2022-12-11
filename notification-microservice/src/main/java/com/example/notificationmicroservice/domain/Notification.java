package com.example.notificationmicroservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "targetId", nullable = false)
    private String targetId;

    @Column(name = "activityId", nullable = false)
    private Long activityId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "position")
    private String position;

    /** Notification constructor.
     *
     * @param userId from user
     * @param targetId to user
     * @param activityId regarding activity
     * @param type of type
     * @param position for position if applicable
     */
    public Notification(String userId, String targetId, Long activityId, String type, String position) {
        this.userId = userId;
        this.targetId = targetId;
        this.activityId = activityId;
        this.type = type;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        Notification that = (Notification) o;
        return Objects.equals(getId(), that.getId()) && getUserId().equals(that.getUserId())
                && getTargetId().equals(that.getTargetId())
                && getActivityId().equals(that.getActivityId()) && getType().equals(that.getType())
                && Objects.equals(getPosition(), that.getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserId(), getTargetId(), getActivityId(), getType(), getPosition());
    }
}