package nl.tudelft.cse.sem.template.user.utils;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class BaseNotification {
    private String userId;
    private String targetId;
    private Long activityId;
    private String position;
    private String type;

}

