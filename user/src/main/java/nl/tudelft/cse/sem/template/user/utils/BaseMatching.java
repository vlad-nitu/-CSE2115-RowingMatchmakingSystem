package nl.tudelft.cse.sem.template.user.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class BaseMatching {

    private String userId;

    private Long activityId;

    private String position;

    private Boolean pending;
}
