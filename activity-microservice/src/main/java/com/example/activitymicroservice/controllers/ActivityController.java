

package com.example.activitymicroservice.controllers;

import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.services.ActivityService;
import com.example.activitymicroservice.utils.TimeSlot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class ActivityController {
    private final transient ActivityService activityService;
    private final transient UserPublisher userPublisher;

    public ActivityController(ActivityService activityService,
                              UserPublisher userPublisher) {
        this.activityService = activityService;
        this.userPublisher = userPublisher;
    }

    @GetMapping("/sendOwnerId/{activityId}")
    public ResponseEntity<String> sentOwnerId(@PathVariable Long activityId) {
        return ResponseEntity.ok(this.activityService.findActivity(activityId).getOwnerId());
    }

    @PostMapping("/sendTimeSlots")
    public ResponseEntity<List<TimeSlot>> sendTimeSlots(@RequestBody List<Long> activityIds) {
        return ResponseEntity.ok(this.activityService.getTimeSlotsByActivityIds(activityIds));
    }
}




