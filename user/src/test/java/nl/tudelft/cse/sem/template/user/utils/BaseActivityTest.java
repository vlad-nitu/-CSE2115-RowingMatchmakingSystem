package nl.tudelft.cse.sem.template.user.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BaseActivityTest {

    @Test
    void constructorTest() {
        Long activityId = 1L;
        String ownerId = "RaduNic";
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        List<String> positions = List.of("cox", "coach");
        String certificate = "C4";
        boolean isCompetitive = true;
        char gender = 'M';
        String organisation = "SEM33A";

        BaseActivity baseActivity = new BaseActivity(activityId,
                ownerId,
                timeSlot,
                positions,
                certificate,
                isCompetitive,
                gender,
                organisation);

        assertNotNull(baseActivity);
    }

}