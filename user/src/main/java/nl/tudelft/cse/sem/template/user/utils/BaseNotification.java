package nl.tudelft.cse.sem.template.user.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BaseNotification {
    private String userId;
    private String targetId;
    private Long activityId;
    private String position;
    private String type;

}

