package nl.tudelft.cse.sem.template.user.utils;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
public class BaseMatching {
    private String userId;

    private Long activityId;

    private String position;

    private Boolean pending;
}
