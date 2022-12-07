package com.example.micro.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class BaseNotification {
    private String userId;
    private String targetId;
    private Long activityId;
    private String position;
    private String type;



}
