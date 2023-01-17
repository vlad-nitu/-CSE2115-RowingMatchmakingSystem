package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.utils.ActivityContext;

import java.io.InvalidObjectException;

public interface Validator {
    void setNext(Validator handler);

    boolean handle(ActivityContext context)
            throws InvalidObjectException;
}