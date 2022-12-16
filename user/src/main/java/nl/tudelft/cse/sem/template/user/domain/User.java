package nl.tudelft.cse.sem.template.user.domain;

import lombok.*;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user", schema = "Projects_UserMicroservice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    @Id
    @Column(name = "userid")
    private String userId;
    @Column(name = "iscompetitive")
    private boolean isCompetitive;
    @Column(name = "gender")
    private char gender;
    @Column(name = "organisation")
    private String organisation;
    @Column(name = "certificate")
    private String certificate;
    @ElementCollection
    @Column(name = "positions")
    private Set<String> positions;

    @ElementCollection
    @CollectionTable(
            name = "Availability",
            joinColumns = @JoinColumn(name = "userid")
    )
    private Set<TimeSlot> timeSlots;

    @Column(name = "email")
    private String email;
}