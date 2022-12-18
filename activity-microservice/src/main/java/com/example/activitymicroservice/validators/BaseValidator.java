package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;

import java.io.InvalidObjectException;

public abstract class BaseValidator implements Validator {
    private transient Validator next;

    public  void setNext(Validator h) {
        this.next = h;
    }

    protected boolean checkNext(Activity activity, UserPublisher userPublisher,
                                String position, String userId) throws InvalidObjectException {
        if (next == null) {
            return true;
        }
        return next.handle(activity, userPublisher, position, userId);
    }
}
