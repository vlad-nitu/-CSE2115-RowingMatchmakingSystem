package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.utils.ActivityContext;

import java.io.InvalidObjectException;
import java.util.Objects;

public class OrganisationValidator extends BaseValidator {
    @Override
    public boolean handle(ActivityContext context) throws InvalidObjectException {
        if (!Objects.equals(((Competition) context.getActivity()).getOrganisation(), context.getUserPublisher()
                .getOrganisation(context.getUserId()))) {
            throw new InvalidObjectException("The organisation is not suitable");
        }
        return super.checkNext(context);
    }
}
