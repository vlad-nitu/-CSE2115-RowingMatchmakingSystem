package com.example.activitymicroservice.publishers;

import com.example.activitymicroservice.utils.ActivityUtils;
import org.h2.engine.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
public class UserPublisherTest {
    @Mock
    private ActivityUtils activityUtils;

    private UserPublisher userPublisher;
    private String userId;

    @BeforeEach
    void setUp() {
        userPublisher = new UserPublisher(activityUtils);
        userId = "Razvan";
    }

    @Test
    void getCompetitivenessGood() throws Exception {
        Response res = Response.ok(true).build();
        when(activityUtils.getRequest("/sendCompetitiveness/" + userId)).thenReturn(res);
        assertThat(userPublisher.getCompetitiveness(userId)).isTrue();
    }

    @Test
    void getCompetitivenessFail() throws Exception {
        when(activityUtils.getRequest("/sendCompetitiveness/" + userId)).thenThrow(new Exception("NotGood"));
        assertThat(userPublisher.getCompetitiveness(userId)).isFalse();
    }

    @Test
    void getGenderGood() throws Exception {
        Response res = Response.ok('M').build();
        when(activityUtils.getRequest("/sendGender/" + userId)).thenReturn(res);
        assertThat(userPublisher.getGender(userId)).isEqualTo('M');
    }

    @Test
    void getGenderFail() throws Exception {
        when(activityUtils.getRequest("/sendGender/" + userId)).thenThrow(new Exception("NotGood"));
        assertThat(userPublisher.getGender(userId)).isEqualTo('N');
    }

    @Test
    void getOrganisationGood() throws Exception {
        Response res = Response.ok("33a").build();
        when(activityUtils.getRequest("/sendOrganization/" + userId)).thenReturn(res);
        assertThat(userPublisher.getOrganisation(userId)).isEqualTo("33a");
    }

    @Test
    void getOrganisationFail() throws Exception {
        when(activityUtils.getRequest("/sendOrganization/" + userId)).thenThrow(new Exception("NotGood"));
        assertThat(userPublisher.getOrganisation(userId)).isEqualTo("");
    }

    @Test
    void getCertificateGood() throws Exception {
        Response res = Response.ok("C4").build();
        when(activityUtils.getRequest("/sendCertificate/" + userId)).thenReturn(res);
        assertThat(userPublisher.getCertificate(userId)).isEqualTo("C4");
    }

    @Test
    void getCertificateFail() throws Exception {
        when(activityUtils.getRequest("/sendCertificate/" + userId)).thenThrow(new Exception("NotGood"));
        assertThat(userPublisher.getCertificate(userId)).isEqualTo("");
    }

}
