package com.example.activitymicroservice.domain;

import com.example.activitymicroservice.utils.TimeSlot;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.*;

@Entity
@Table(name = "activity", schema = "projects_ActivityMicroservice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
abstract class Activity {
    @Id
    @Column(name = "activityID")
    private Long activityID;
    @Column(name = "ownerID")
    private String ownerID;
    @Column(name = "timeslot")
    private TimeSlot timeSlot;
    @Column(name = "availablePosiitons")
    private List<String> availablePositions;
    @Column(name = "certificate")
    private String certificate;
}
