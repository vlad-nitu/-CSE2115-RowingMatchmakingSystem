package com.example.activitymicroservice.domain;

import lombok.*;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Cometiton extends Activity{
    @Column(name = "isCompetitive")
    private boolean isCompetitive;
    @Column(name = "gender")
    private char gender;
    @Column(name = "organisation")
    private String organisation;
}
