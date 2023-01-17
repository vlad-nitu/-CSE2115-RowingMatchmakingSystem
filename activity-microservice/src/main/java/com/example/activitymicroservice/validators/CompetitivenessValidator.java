package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.utils.ActivityContext;

import java.io.InvalidObjectException;

public class CompetitivenessValidator extends BaseValidator {

    /**
     * Checks is a User can enrol to an Activity based on competitiveness.
     *
     * @param context an ActivityContext object containing an Activity object, an UserPublisher object
     *                and two String Objects referring to the asked position and the UserId
     * @return call to checkNext method
     * @throws InvalidObjectException when the User cannot enrol to the Activity
     */
    public boolean handle(ActivityContext context) throws InvalidObjectException {
        if (((Competition) context.getActivity()).isCompetitive() && !context.getUserPublisher()
                .getCompetitiveness(context.getUserId())) {
            throw new InvalidObjectException("Competitiveness does not match");
        }
        return super.checkNext(context);
    }
}
