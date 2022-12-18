package com.example.activitymicroservice.domain;

import com.example.activitymicroservice.utils.TimeSlot;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;



@Entity
@Table(name = "activty", schema = "projects_ActivityMicroservice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Training.class, name = "training"),
        @JsonSubTypes.Type(value = Competition.class, name = "competition")
})
public abstract class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long activityId;
    private String ownerId;
    @Transient
    private TimeSlot timeSlot;
    @ElementCollection
    private Set<String> availablePositions;
    private String certificate;
    private String type; // used for deserialization of abstract class instance
}
