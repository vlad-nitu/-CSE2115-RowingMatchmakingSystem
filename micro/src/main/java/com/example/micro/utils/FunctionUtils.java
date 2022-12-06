package com.example.micro.utils;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class FunctionUtils {
    /**
     * Eliminates the time slots that overlap with already existing ones.
     *
     * @param timeSlots the time slots that should be checked
     * @param occTimeSlots existing time slots
     * @return a list of non-overlapping times lots
     */
    public static List<TimeSlot> filterTimeSlots(List<TimeSlot> timeSlots, List<TimeSlot> occTimeSlots) {
        List<TimeSlot> filtered = new ArrayList<>();
        for (TimeSlot t : timeSlots) {
            boolean isOverlap = false;
            for (TimeSlot o : occTimeSlots) {
                if (t.overlaps(o)) {
                    isOverlap ^= true;
                    break;
                }
            }
            if (!isOverlap) {
                filtered.add(t);
            }
        }
        return filtered;
    }
}
