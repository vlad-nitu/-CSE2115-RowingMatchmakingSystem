package com.example.micro.utils;

import java.io.Serializable;
import java.util.Objects;

public class CompositeKey implements Serializable {
    private String userId;
    private Long activityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeKey that = (CompositeKey) o;
        return userId.equals(that.userId) && activityId.equals(that.activityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, activityId);
    }
}
