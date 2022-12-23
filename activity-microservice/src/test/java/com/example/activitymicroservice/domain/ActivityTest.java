package com.example.activitymicroservice.domain;

import com.example.activitymicroservice.utils.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    private Activity training;
    private Activity training2;

    @BeforeEach
    void setUp() {
        training = new Training();
        training.setActivityId(1L);
        training.setOwnerId("Vlad");
        training.setTimeSlot(new TimeSlot(LocalDateTime.of(2012, 12, 22, 10, 10),
                LocalDateTime.of(2012, 12, 22, 11, 11)));
        training.setPositions(List.of("cox", "coach"));
        training.setCertificate("C4");
        training2 = new Training();
        training2.setActivityId(1L);
        training2.setOwnerId("Vlad");
        training2.setTimeSlot(new TimeSlot(LocalDateTime.of(2012, 12, 22, 10, 10),
                LocalDateTime.of(2012, 12, 22, 11, 11)));
        training2.setPositions(List.of("cox", "coach"));
        training2.setCertificate("C4");
    }

    @Test
    void testEqualsFails() {
        Activity training = new Training();
        training.setOwnerId("Vlad");
        Activity training2 = new Training();
        training2.setOwnerId("Radu");
        assertThat(training).isNotEqualTo(training2);
    }

    @Test
    void testEquals() {
        Activity training = new Training();
        training.setOwnerId("Vlad");
        Activity training2 = new Training();
        training2.setOwnerId("Vlad");
        assertThat(training).isEqualTo(training2);
    }

    @Test
    void testEqualsLargeFalse() {
        assertThat(training).isEqualTo(training2);
    }

    @Test
    void testEqualsLargePasses() {
        Activity training = new Training();
        training.setActivityId(1L);
        training.setOwnerId("Vlad");
        training.setTimeSlot(new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30)));
        training.setPositions(List.of("cox", "coach"));
        training.setCertificate("C4");
        Activity training2 = new Training();
        training2.setActivityId(1L);
        training2.setOwnerId("Vlad");
        training2.setTimeSlot(new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30)));
        training2.setPositions(List.of("cox", "coach"));
        training2.setCertificate("C4");
        assertThat(training).isNotEqualTo(training2);
    }

    @Test
    void testEqualsLargeCombinations() {
        Activity training = new Training();
        training.setActivityId(1L);
        training.setOwnerId("Vlad");
        training.setTimeSlot(new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30)));
        training.setPositions(List.of("cox", "coach"));
        training.setCertificate("C4");
        Activity training2 = new Training();
        training2.setActivityId(1L);
        training2.setOwnerId("Vlad");
        training2.setTimeSlot(new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30)));
        training2.setPositions(List.of("cox", "coach"));
        training2.setCertificate("C4");
        assertThat(training).isNotEqualTo(training2);
        training2.setActivityId(2L);
        assertThat(training).isNotEqualTo(training2);
        training2.setActivityId(training.getActivityId());
        training2.setPositions(List.of("other"));
        assertThat(training).isNotEqualTo(training2);
        training2.setPositions(training.getPositions());
        training2.setCertificate("8+");
        assertThat(training).isNotEqualTo(training2);
    }

    @Test
    void diffPositionsTest() {
        training2.setPositions(List.of("other", "positions"));
        assertThat(training).isNotEqualTo(training2);
    }

    @Test
    void diffCertificateTest() {
        training2.setCertificate("dummyCertificate");
        assertThat(training).isNotEqualTo(training2);
    }


    @Test
    void testEqualsDiffTypes() {
        Activity activity1 = new Competition(true, 'M', "SEM33A");
        assertThat(activity1.equals("Abc")).isFalse();
    }

    @Test
    void testHashCodes() {
        Activity activity1 = new Competition(true, 'M', "SEM33A");
        assertThat(activity1.hashCode()).isEqualTo(28629151);
    }

}