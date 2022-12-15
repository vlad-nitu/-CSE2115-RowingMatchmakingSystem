package com.example.activitymicroservice.services;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.repositories.ActivityRepository;
import com.example.activitymicroservice.utils.TimeSlot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ActivityService {

    private final transient ActivityRepository activityRepository;

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    public Activity save(Activity matching) {
        return activityRepository.save(matching);
    }

    public List<TimeSlot> findTimeslotsByActivityId() {
        return activityRepository.findAll().stream().map(x -> x.getTimeSlot()).collect(Collectors.toList());
    }

    public Activity findActivity(Long activityId) {
        return activityRepository.findById(activityId).get();
    }

    public void deleteById(Long activityId) {
        activityRepository.deleteById(activityId);
    }

    public List<TimeSlot> getTimeSlotsByActivityIds(List<Long> activityIds){
        List<TimeSlot> timeSlots = new ArrayList<>();
        for(Long activityId : activityIds){
            timeSlots.add(findActivity(activityId).getTimeSlot());
        }
        return timeSlots;
    }

}
