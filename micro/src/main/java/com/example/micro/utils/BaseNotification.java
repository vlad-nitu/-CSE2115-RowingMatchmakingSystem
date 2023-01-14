package com.example.micro.utils;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class BaseNotification {
    private String userId;
    private String targetId;
    private Long activityId;
    private String position;
    private String type;



}
