package com.example.notificationmicroservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
    @JsonIgnore
    private Long id;

    @Column(name = "userId", nullable = false)
    @NotBlank(message = "userId is mandatory")
    private String userId;

    @Column(name = "targetId", nullable = false)
    @NotBlank(message = "targetId is mandatory")
    private String targetId;

    @Column(name = "activityId", nullable = false)
    @NotBlank(message = "activityId is mandatory")
    private Long activityId;

    @Column(name = "type", nullable = false)
    @NotBlank(message = "type is mandatory")
    private String type;

    @Column(name = "position")
    private String position;

    public Notification(String userId, String targetId, Long activityId, String type, String position) {
        this.userId = userId;
        this.targetId = targetId;
        this.activityId = activityId;
        this.type = type;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(getId(), that.getId()) && getUserId().equals(that.getUserId()) && getTargetId().equals(that.getTargetId()) && getActivityId().equals(that.getActivityId()) && getType().equals(that.getType()) && Objects.equals(getPosition(), that.getPosition());
    }
}