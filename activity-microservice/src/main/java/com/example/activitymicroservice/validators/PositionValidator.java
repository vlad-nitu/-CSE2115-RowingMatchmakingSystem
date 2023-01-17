package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.utils.ActivityContext;

import java.io.InvalidObjectException;

public class PositionValidator extends BaseValidator {
    @Override
    public boolean handle(ActivityContext context) throws InvalidObjectException {
        if (!context.getUserPublisher().getPositions(context.getUserId()).contains(context.getPosition())) {
            throw new InvalidObjectException("Position is not available or does not exist");
        }
        return super.checkNext(context);
    }
}
