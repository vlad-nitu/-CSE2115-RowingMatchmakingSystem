package com.example.activitymicroservice.domain;

import com.example.activitymicroservice.utils.TimeSlot;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "activty", schema = "projects_ActivityMicroservice")
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
    @ElementCollection
    private List<String> availablePositions;
    private String certificate;

    /**
     * Default constructor.
     *
     * @param ownerId the ID of the owner
     * @param timeSlot the timeSlot of the activity
     * @param availablePositions the available positions in the activity
     * @param certificate the certificate needed for the activity
     */
    public Activity(String ownerId, TimeSlot timeSlot, List<String> availablePositions, String certificate) {
        this.ownerId = ownerId;
        this.timeSlot = timeSlot;
        this.availablePositions = availablePositions;
        this.certificate = certificate;
    }
}
