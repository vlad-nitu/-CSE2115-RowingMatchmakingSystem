package com.example.activitymicroservice.publishers;

import com.example.activitymicroservice.utils.ActivityUtils;
import com.example.activitymicroservice.utils.TimeSlot;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserPublisherTest {
    @Mock
    private ActivityUtils activityUtils;

    private UserPublisher userPublisher;
    private String userId;

    private Set<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        userPublisher = new UserPublisher(activityUtils);
        userId = "Razvan";

        timeSlots = new HashSet<TimeSlot>();
        timeSlots.add(new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0)));
        timeSlots.add(new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0)));
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

    @Test
    void getPositionsGood() throws Exception {
        Set<String> list = Set.of("cox", "rower");
        Response res = Response.ok(list).build();
        when(activityUtils.getRequest("/sendPositions/" + userId)).thenReturn(res);
        assertThat(userPublisher.getPositions(userId)).isEqualTo(list);
    }

    @Test
    void getPositionsFail() throws Exception {
        when(activityUtils.getRequest("/sendPositions/" + userId)).thenThrow(new Exception("NotGood"));
        assertThat(userPublisher.getPositions(userId).isEmpty()).isTrue();
    }

    @Test
    void getTimeSlotsGood() throws Exception {
        Response res = Response.ok(new ArrayList<>(timeSlots)).build();
        when(activityUtils.getRequest("/sendTimeSlots/" + userId)).thenReturn(res);
        assertThat(userPublisher.getTimeslots(userId)).isEqualTo(new ArrayList<>(timeSlots));
    }

    @Test
    void getTimeSlotsFail() throws Exception {
        when(activityUtils.getRequest("/sendTimeSlots/" + userId)).thenThrow(new Exception("NotGood"));
        assertThat(userPublisher.getTimeslots(userId).isEmpty()).isTrue();
    }
}
