package com.example.micro.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CompositeKey implements Serializable {
    private String userId;
    private Long activityId;
    private String position;
}
