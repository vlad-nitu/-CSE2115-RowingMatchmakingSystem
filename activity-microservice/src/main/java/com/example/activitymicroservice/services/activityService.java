package com.example.activitymicroservice.services;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.repositories.ActivityRepository;
import com.example.activitymicroservice.utils.TimeSlot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
        return activityRepository.findAll().stream().map(Activity::getTimeSlot).collect(Collectors.toList());
    }
}
