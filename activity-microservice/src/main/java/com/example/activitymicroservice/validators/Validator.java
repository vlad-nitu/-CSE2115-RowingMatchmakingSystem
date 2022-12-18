package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;

import java.io.InvalidObjectException;

public interface Validator {
    void setNext(Validator handler);

    boolean handle(Activity activity, UserPublisher userPublisher, String position, String userId)
            throws InvalidObjectException;
}
