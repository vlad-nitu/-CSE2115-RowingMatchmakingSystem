package com.example.micro.domain;


import javax.persistence.*;

import com.example.micro.utils.CompositeKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "matching", schema = "projects_MatchingMicroservice")
@IdClass(CompositeKey.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Matching {
    @Id
    @Column(name = "userid")
    private String userId;
    @Id
    @Column(name = "activityid")
    private Long activityId;
    @Column(name = "position")
    private String position;
    @Column(name = "pending")
    private boolean pending;
}
