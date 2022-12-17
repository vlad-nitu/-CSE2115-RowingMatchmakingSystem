package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Cometiton;
import com.example.activitymicroservice.publishers.UserPublisher;

import java.io.InvalidObjectException;

public class CompetitivenessValidator extends BaseValidator {

    /**
     * Checks is a User can enrol to an Activity based on competitiveness.
     *
     * @param activity Activity object
     * @param userPublisher UserPublisher object
     * @param position String object, the position the User wants to apply for
     * @param userId the ID of the given User
     * @return call to checkNext method
     * @throws InvalidObjectException when the User cannot enrol to the Activity
     */
    public boolean handle(Activity activity, UserPublisher userPublisher,
                          String position, String userId) throws InvalidObjectException {
        if (activity instanceof Cometiton) {
            if (((Cometiton) activity).isCompetitive() != userPublisher.getCompetitiveness(userId)) {
                throw new InvalidObjectException("Competitiveness does not match");
            }
        }
        return super.checkNext(activity, userPublisher, position, userId);
    }
}
