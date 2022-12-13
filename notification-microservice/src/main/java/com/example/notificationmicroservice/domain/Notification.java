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
@EqualsAndHashCode
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
    @NotBlank(message = "userId is mandatory and cannot be blank")
    private String userId;

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
    private String position;

    /** This Notification constructor is needed because I can't use.
     * All args constructor since id is assigned by the db.
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
}