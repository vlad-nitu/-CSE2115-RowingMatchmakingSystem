package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CertificateValidator extends BaseValidator {
    //The latter in the list the more priority it has
    private static List<String> certificates = new ArrayList<>(List.of("C4", "4+", "8+"));

    @Override
    public boolean handle(Activity activity, UserPublisher userPublisher,
                          String position, String userId) throws InvalidObjectException {
        String userCertificate = userPublisher.getCertificate(userId);
        if (!certificates.contains(activity.getCertificate()) || !certificates.contains(userCertificate)) {
            throw new InvalidObjectException("Certificate is not suitable");
        }
        if (certificates.indexOf(activity.getCertificate()) > certificates.indexOf(userCertificate)) {
            throw new InvalidObjectException("You are not entitled to steer this type of boat");
        }

        return super.checkNext(activity, userPublisher, position, userId);
    }

    public static void updateCertificateList(List<String> list) {
        certificates = new ArrayList<>(list);
    }

    public static void addCertificate(String certificate) {
        certificates.add(certificate);
    }
}
