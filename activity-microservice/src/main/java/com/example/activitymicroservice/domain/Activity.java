package com.example.activitymicroservice.domain;

import com.example.activitymicroservice.utils.TimeSlot;
import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "activity", schema = "projects_ActivityMicroservice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long activityId;
    private String ownerId;
    @Transient
    private TimeSlot timeSlot;
    private List<String> availablePositions;
    private String certificate;
}
