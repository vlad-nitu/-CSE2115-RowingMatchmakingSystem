package com.example.micro.utils;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class BaseNotification {
    @Getter
    private String userId;
    @Getter
    private String targetId;
    @Getter
    private Long activityId;
    @Getter
    private String position;
    @Getter
    private String type;
}
