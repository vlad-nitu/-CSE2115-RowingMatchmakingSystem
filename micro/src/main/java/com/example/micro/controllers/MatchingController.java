package com.example.micro.controllers;

import com.example.micro.domain.Matching;

import java.util.List;

import com.example.micro.publishers.ActivityPublisher;
import com.example.micro.publishers.NotificationPublisher;
import com.example.micro.publishers.UserPublisher;
import com.example.micro.services.MatchingServiceImpl;
import com.example.micro.utils.TimeSlot;
import com.example.micro.utils.FunctionUtils;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MatchingController {

    private final transient MatchingServiceImpl matchingServiceImpl;
    private final transient ActivityPublisher activityPublisher;
    private final transient UserPublisher userPublisher;
    private final transient NotificationPublisher notificationPublisher;

    public MatchingController(MatchingServiceImpl matchingServiceImpl, ActivityPublisher activityPublisher, UserPublisher userPublisher, NotificationPublisher notificationPublisher) {
        this.matchingServiceImpl = matchingServiceImpl;
        this.activityPublisher = activityPublisher;
        this.userPublisher = userPublisher;
        this.notificationPublisher = notificationPublisher;
    }

    @GetMapping ("/getAvailableActivities/{userId}/{timeSlots}")
    public ResponseEntity<List<Pair<Long, String>>> getAvailableActivities(@PathVariable String userId, @PathVariable List<TimeSlot> timeSlots) {
        List<Long> selectedActivities = matchingServiceImpl.findActivitiesByUserId(userId);
        List<TimeSlot> occTimeSlots = activityPublisher.getTimeSlots(selectedActivities);
        List<TimeSlot> newTimeSlots = FunctionUtils.filterTimeSlots(timeSlots, occTimeSlots);
        List<Pair<Long, String>> possibleActivities = activityPublisher.getAvailableActivities(userId, newTimeSlots);
        userPublisher.sendAvailableActivities(possibleActivities);
        return ResponseEntity.ok(possibleActivities);
    }


    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello world!");
    }


    /**
     * Find all matchings.
     *
     * @return - Response of a list of matchings
     */
    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Matching>> findAllMatchings() {
        return ResponseEntity.ok(matchingServiceImpl.findAll());
    }

    /**
     * Save a matching.
     *
     * @param matching - Matching obj
     * @return - saved Matching obj
     */
    @PostMapping("/save")
    public ResponseEntity<Matching> saveMatching(
            @RequestBody Matching matching
    ) {
        return ResponseEntity.ok(
                matchingServiceImpl.save(matching)
        );
    }

}