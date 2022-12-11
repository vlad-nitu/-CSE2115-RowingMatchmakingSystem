package com.example.micro.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.micro.domain.Matching;
import com.example.micro.utils.MatchingUtils;
import com.example.micro.utils.Pair;
import com.example.micro.utils.TimeSlot;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

public class FunctionUtilTest {
    @Test
    public void filterTimeSlotsTest() {
        List<TimeSlot> timeSlots = new ArrayList<>();
        TimeSlot time1 = new TimeSlot(LocalDateTime.of(2003, 1, 1, 0, 0),
                LocalDateTime.of(2003, 12, 1, 0, 0));
        TimeSlot time2 = new TimeSlot(LocalDateTime.of(2003, 6, 1, 0, 0),
                LocalDateTime.of(2004, 6, 1, 0, 0));
        TimeSlot time3 = new TimeSlot(LocalDateTime.of(2004, 1, 1, 0, 0),
                LocalDateTime.of(2004, 12, 1, 0, 0));
        TimeSlot time4 = new TimeSlot(LocalDateTime.of(2005, 1, 1, 0, 0),
                LocalDateTime.of(2005, 12, 1, 0, 0));
        List<TimeSlot> list1 = new ArrayList<>(Arrays.asList(time1, time3));
        List<TimeSlot> list2 = new ArrayList<>(List.of(time4));
        List<TimeSlot> list3 = new ArrayList<>(Arrays.asList(time3, time4));
        List<TimeSlot> list4 = new ArrayList<>(List.of(time2));
        List<TimeSlot> solutionFull = new ArrayList<>(Arrays.asList(time1, time3));
        List<TimeSlot> solutionWithout = new ArrayList<>(List.of(time4));
        assertThat(FunctionUtils.filterTimeSlots(list1, list2)).isEqualTo(solutionFull);
        assertThat(FunctionUtils.filterTimeSlots(list3, list4)).isEqualTo(solutionWithout);
    }
}
