package com.example.micro.utils;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class BaseNotification {
    private String targetId;
    private Long activityId;
    private String position;
    private String type;
}
