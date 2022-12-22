package com.example.activitymicroservice.domain;

import com.example.activitymicroservice.utils.TimeSlot;

import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.ToString;


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
public abstract class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long activityId;
    private String ownerId;

    @Embedded
    private TimeSlot timeSlot;

    @ElementCollection
    @NotNull(message = "Positions list cannot be null")
    @Column(name = "positions")
    private List<@NotBlank(message = "Position cannot be blank")
                @NotNull(message = "Position cannot be null")
                    @Size(min = 3, max = 20, message = "Position name must be between 3 and 20 chars") String> positions;

    private String certificate;

    //Added this because we need an equals method for testing and the Lombok
    //Equals and hashcode was throwing a scary warning regarding JPA entities
    //Not working well with it
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Activity)) {
            return false;
        }
        Activity activity = (Activity) o;
        return Objects.equals(getActivityId(), activity.getActivityId())
                && Objects.equals(getOwnerId(), activity.getOwnerId())
                && Objects.equals(getTimeSlot(), activity.getTimeSlot())
                && Objects.equals(getPositions(), activity.getPositions())
                && Objects.equals(getCertificate(), activity.getCertificate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getActivityId(), getOwnerId(), getTimeSlot(), getPositions(), getCertificate());
    }
}
