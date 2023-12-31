package nl.tudelft.cse.sem.template.user.domain;

import lombok.*;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;

import javax.persistence.*;
import javax.validation.constraints.*;
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
    @NotBlank(message = "User ID is mandatory and cannot be blank")
    @NotNull(message = "User ID is mandatory and cannot be null")
    @Size(min = 3, max = 20, message = "User ID must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z\\d]+$", message = "userId must not contain special characters nor spaces")
    private String userId;

    @NotNull(message = "Competitiveness level is mandatory and cannot be null")
    @Column(name = "iscompetitive")
    private Boolean isCompetitive;

    @Column(name = "gender")
    private char gender;

    @NotBlank(message = "Organisation is mandatory and cannot be blank")
    @NotNull(message = "Organisation is mandatory and cannot be null")
    @Size(min = 5, max = 30, message = "Organisation name must be between 5 and 30 characters")
    @Column(name = "organisation")
    private String organisation;


    @NotNull(message = "Certificate cannot be null")
    @Size(max = 30, message = "Certificate name must be shorter than 30 characters")
    @Column(name = "certificate")
    private String certificate;

    @Email
    @NotNull(message = "E-mail cannot be null")
    @NotBlank(message = "E-mail cannot be blank")
    @Column(name = "email")
    private String email;

    @ElementCollection
    @NotNull(message = "Positions list cannot be null")
    @Column(name = "positions")
    private Set<@NotBlank(message = "Position cannot be blank")
        @NotNull(message = "Position cannot be null")
            @Size(min = 3, max = 20, message = "Position name must be between 3 and 20 characters") String> positions;


    @ElementCollection
    @CollectionTable(
            name = "Availability",
            joinColumns = @JoinColumn(name = "userid")
    )
    private Set<@NotNull(message = "Time slot cannot be null") TimeSlot> timeSlots;
}