package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.publishers.UserPublisher;

import java.io.InvalidObjectException;

public class GenderValidator extends BaseValidator {

    /**
     * Checks is a User can enrol to an Activity based on gender.
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
        if (activity instanceof Competition
                && ((Competition) activity).getGender() != userPublisher.getGender(userId)) {
            throw new InvalidObjectException("The gender is not suitable");
        }
        return super.checkNext(activity, userPublisher, position, userId);
    }
}
