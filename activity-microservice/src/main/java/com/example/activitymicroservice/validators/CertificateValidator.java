package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;

import java.io.InvalidObjectException;
import java.util.Objects;

public class CertificateValidator extends BaseValidator {
    @Override
    public boolean handle(Activity activity, UserPublisher userPublisher,
                          String position, String userId) throws InvalidObjectException {
        if (!Objects.equals(activity.getCertificate(), userPublisher.getCertificate(userId))) {
            throw new InvalidObjectException("Certificate is not suitable");
        }
        return super.checkNext(activity, userPublisher, position, userId);
    }
}
