package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.utils.ActivityContext;

import java.io.InvalidObjectException;

public abstract class BaseValidator implements Validator {
    private transient Validator next;

    public  void setNext(Validator h) {
        this.next = h;
    }

    protected boolean checkNext(ActivityContext context) throws InvalidObjectException {
        if (next == null) {
            return true;
        }
        return next.handle(context);
    }
}