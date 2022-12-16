package com.example.micro.domain;

import com.example.micro.utils.CompositeKey;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "matching", schema = "projects_MatchingMicroservice")
@IdClass(CompositeKey.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Matching {
    @Id
    @NotBlank(message = "userId is mandatory, thus it cannot be blank.")
    @Column(name = "userid", nullable = false)
    private String userId;
    @Id
    @NotNull(message = "activityId is mandatory, thus it cannot be null.")
    @Column(name = "activityid", nullable = false)
    private Long activityId;
    @Id
    @NotBlank(message = "position is mandatory, thus it cannot be blank.")
    @Column(name = "position", nullable = false)
    private String position;
    @Column(name = "pending")
    private Boolean pending;
}
