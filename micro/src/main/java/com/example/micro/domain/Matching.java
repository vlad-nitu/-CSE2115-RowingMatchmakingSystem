package com.example.micro.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Matching {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String userID;
    private Long activityID;
    private String position;
    private boolean pending;
}
