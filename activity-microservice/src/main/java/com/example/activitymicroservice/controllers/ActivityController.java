

package com.example.activitymicroservice.controllers;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.services.ActivityService;
import com.example.activitymicroservice.utils.Pair;
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

    /**
     * API Endpoint that performs a POST request in order to update
     * an Activity after a User has occupied a certain spot.
     *
     * @param posTaken Pair of a Long and String object representing the ID of the activity and the
     *                 position occupied respectively
     * @return ResponseEntity object that specifies if the request could be done
     */
    @PostMapping("/takeAvailableSpot")
    public ResponseEntity.BodyBuilder takeAvailableSpot(@RequestBody Pair<Long, String> posTaken) {
        try {
            this.activityService.takeSpot(posTaken);
            return ResponseEntity.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return  ResponseEntity.badRequest();
        }
    }

    /**
     * API Endpoint that performs a GET request in order to
     * check if a User is eligible for a certain Activity.
     *
     * @param userId String object representing the User's ID
     * @param activityId Long object representing the Activity's ID
     * @param position String object representing the position the User is applying to
     * @return  ResponseEntity object with a boolean to certify if the User is eligible for that Activity or not
     */
    @GetMapping("/check/{userId}/{activityId}/{position}")
    public ResponseEntity<Boolean> check(@PathVariable String userId,
                                         @PathVariable Long activityId, @PathVariable String position) {
        Activity activity = this.activityService.findActivity(activityId);
        Character gender = this.userPublisher.getGender(userId);
        boolean competitiveness = this.userPublisher.getCompetitiveness(userId);
        String organisation = this.userPublisher.getOrganisation(userId);
        String certificate = this.userPublisher.getCertificate(userId);
        List<String> listPositions = this.userPublisher.getPositions(userId);
        return ResponseEntity.ok(this.activityService.checkUser(activity, gender,
                certificate, organisation, competitiveness, listPositions, position));
    }
}




