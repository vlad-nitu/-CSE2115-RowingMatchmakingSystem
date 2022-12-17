package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Cometiton;
import com.example.activitymicroservice.publishers.UserPublisher;

import java.io.InvalidObjectException;
import java.util.Objects;

public class OrganisationValidator extends BaseValidator {
    @Override
    public boolean handle(Activity activity, UserPublisher userPublisher,
                          String position, String userId) throws InvalidObjectException {
        if (activity instanceof Cometiton
                && !Objects.equals(((Cometiton) activity).getOrganisation(), userPublisher.getOrganisation(userId))) {
            throw new InvalidObjectException("The organisation is not suitable");
        }
        return super.checkNext(activity, userPublisher, position, userId);
    }
}
