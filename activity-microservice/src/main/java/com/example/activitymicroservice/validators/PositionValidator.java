package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;

import java.io.InvalidObjectException;

public class PositionValidator extends BaseValidator {
    @Override
    public boolean handle(Activity activity, UserPublisher userPublisher,
                          String position, String userId) throws InvalidObjectException {
        if (!userPublisher.getPositions(userId).contains(position)) {
            throw new InvalidObjectException("Position is not available or does not exist");
        }
        return super.checkNext(activity, userPublisher, position, userId);
    }
}
