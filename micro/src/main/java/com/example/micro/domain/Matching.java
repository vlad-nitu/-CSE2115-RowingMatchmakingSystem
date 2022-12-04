package com.example.micro.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "matching", schema = "projects_MatchingMicroservice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Matching {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "userid")
    private String userId;
    @Column(name = "activityid")
    private Long activityId;
    private String position;
    private boolean pending;
}
