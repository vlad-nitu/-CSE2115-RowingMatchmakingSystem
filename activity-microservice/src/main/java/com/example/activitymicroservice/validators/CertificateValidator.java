package com.example.activitymicroservice.validators;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.utils.ActivityContext;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CertificateValidator extends BaseValidator {
    //The latter in the list the more priority it has
    private static List<String> certificates = new ArrayList<>(List.of("C4", "4+", "8+"));

    @Override
    public boolean handle(ActivityContext context) throws InvalidObjectException {
        String userCertificate = context.getUserPublisher().getCertificate(context.getUserId());
        if (!certificates.contains(context.getActivity().getCertificate()) || !certificates.contains(userCertificate)) {
            throw new InvalidObjectException("Certificate is not suitable");
        }
        if (certificates.indexOf(context.getActivity().getCertificate()) > certificates.indexOf(userCertificate)) {
            throw new InvalidObjectException("You are not entitled to steer this type of boat");
        }

        return super.checkNext(context);
    }

    public static void updateCertificateList(List<String> list) {
        certificates = new ArrayList<>(list);
    }

    public static void addCertificate(String certificate) {
        certificates.add(certificate);
    }
}
