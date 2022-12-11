package com.example.activitymicroservice.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Competition extends Activity {
    @Column(name = "isCompetitive")
    private boolean isCompetitive;
    @Column(name = "gender")
    private char gender;
    @Column(name = "organisation")
    private String organisation;
}
