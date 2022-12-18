package com.example.activitymicroservice.domain;

import com.example.activitymicroservice.utils.TimeSlot;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;



@Entity
@Table(name = "activity", schema = "projects_Activty-microservice")
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
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long activityId;
    private String ownerId;
    @Transient

    private TimeSlot timeSlot;

    @ElementCollection
    @NotNull(message = "Positions list cannot be null")
    @Column(name = "positions")
    private Set<@NotBlank(message = "Position cannot be blank")
    @NotNull(message = "Position cannot be null")
    @Size(min = 3, max = 20, message = "Position name must be between 3 and 20 characters") String> positions;

    private String certificate;
    private String type; // used for deserialization of abstract class instance
}
