package com.example.micro.controllers;

import com.example.micro.authentication.AuthManager;
import com.example.micro.domain.Matching;
import com.example.micro.publishers.ActivityPublisher;
import com.example.micro.publishers.NotificationPublisher;
import com.example.micro.services.MatchingServiceImpl;
import com.example.micro.utils.FunctionUtils;
import com.example.micro.utils.Pair;
import com.example.micro.utils.TimeSlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class MatchingController {

    private final transient MatchingServiceImpl matchingServiceImpl;
    private final transient ActivityPublisher activityPublisher;
    private final transient NotificationPublisher notificationPublisher;
    private final transient AuthManager authManger;

    /**
     * All arguments constructor, injects the main service component and all 3 publishers into controller.
     *
     * @param matchingServiceImpl   - matching service implementation
     * @param activityPublisher     - for communication with Activity microservice through API Endpoints
     * @param notificationPublisher - for communication with Notification microservice through API Endpoints
     */
    public MatchingController(MatchingServiceImpl matchingServiceImpl,
                              ActivityPublisher activityPublisher,
                              NotificationPublisher notificationPublisher,
                              AuthManager authManager) {
        this.matchingServiceImpl = matchingServiceImpl;
        this.activityPublisher = activityPublisher;
        this.notificationPublisher = notificationPublisher;
        this.authManger = authManager;
    }

    /**
     * API Endpoint that performs a GET request in order to obtain all the available activities a user can be matched to,
     * based on his/her specified timeslot.
     *
     * @param userId    - String object representing the User's ID
     * @param timeSlots - Timeslot object representing the start time and end time a user is available between
     * @return - ResponseEntity object with a message composed of all the possible activities a user can be matched to.
     */
    @PostMapping("/getAvailableActivities/{userId}")
    public ResponseEntity<List<Pair<Long, String>>> getAvailableActivities(@PathVariable String userId,
                                                                           @RequestBody List<TimeSlot> timeSlots) {
        List<Long> selectedActivities = matchingServiceImpl.findActivitiesByUserId(userId);
        List<TimeSlot> occTimeSlots = activityPublisher.getTimeSlots(selectedActivities);
        List<TimeSlot> newTimeSlots = FunctionUtils.filterTimeSlots(timeSlots, occTimeSlots);
        List<Pair<Long, String>> possibleActivities = activityPublisher.getAvailableActivities(userId, newTimeSlots);
        return ResponseEntity.ok(possibleActivities);
    }

    /**
     * API Endpoint that performs a POST request in order to specify the activity that he wants to take part in, after
     * being matched as a result of his previous request.
     * Note: If a user chooses multiple activities, there will be created a single request per activity
     *
     * @param matching - Matching object representing the User-Activity pair
     * @return - ResponseEntity object with a message composed of the saved matching.
     */
    @PostMapping("/chooseActivity")
    public ResponseEntity<Matching> chooseActivity(@Valid @RequestBody Matching matching) {
        if (!activityPublisher.check(matching)
                || matchingServiceImpl.findMatchingWithPendingFalse(matching.getUserId(), matching.getActivityId())
                .isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        matching.setPending(true);
        Matching savedMatching = matchingServiceImpl.save(matching);
        String targetId = activityPublisher.getOwnerId(matching.getActivityId());
        notificationPublisher.notifyUser(matching.getUserId(), targetId,
                matching.getActivityId(), matching.getPosition(), "notifyOwner");
        return ResponseEntity.ok(savedMatching);
    }

    /**
     * API Endpoint performs a POST request in order for an owner of a specific activity
     * to specify that it accepts or declines a user who is asking to take part in his/her activity in a previous request.
     * @param matching Matching object representing the User-Activity pair
     * @param senderId The id of the person that send the request
     * @param type The type of the request
     * @return ResponseEntity object with a message composed of the matching that was accepted or declined
     */
    @PostMapping("/decideMatchAccept/{senderId}/{type}")
    public ResponseEntity<Matching> chooseMatch(@Valid @RequestBody Matching matching, @PathVariable String senderId, @PathVariable String type){
        String ownerId = activityPublisher.getOwnerId(matching.getActivityId());
        if(!authManger.getNetId().equals(senderId)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (ownerId.equals(senderId) ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!matchingServiceImpl.checkId(matching.getUserId(), matching.getActivityId(), matching.getPosition())){
            return ResponseEntity.badRequest().build();
        }
        if (type.equals("accept")){
            return chooseMatchAccept(matching);
        }
        if(type.equals("decline")){
            return chooseMatchDecline(matching);
        }
        return ResponseEntity.badRequest().build();
    }


    /**
     * API Endpoint that performs a POST request in order for an owner of a specific activity
     * to specify that it accepts a user who is asking to take part in his/her activity in a previous request.
     *
     * @param matching - Matching object representing the User-Activity pair
     * @return - ResponseEntity object with a message composed of the matching that was accepted
     */
    public ResponseEntity<Matching> chooseMatchAccept(Matching matching) {
        matchingServiceImpl.deleteById(matching.getUserId(), matching.getActivityId(), matching.getPosition());
        matching.setPending(false);
        Matching savedMatching = matchingServiceImpl.save(matching);
        activityPublisher.takeAvailableSpot(matching.getActivityId(), matching.getPosition());
        // User is also the target
        String userId = matching.getUserId();
        notificationPublisher.notifyUser(userId, userId, matching.getActivityId(), matching.getPosition(), "notifyUser");
        return ResponseEntity.ok(savedMatching);
    }

    /**
     * API Endpoint that performs a POST request in order for an owner of a specific activity
     * to specify that it decline a user who is asking to take part in his/her activity in a previous request.
     *
     * @param matching - Matching object representing the User-Activity pair
     * @return - ResponseEntity object with a message composed of the matching that was declined
     */
    public ResponseEntity<Matching> chooseMatchDecline(Matching matching) {
        matchingServiceImpl.deleteById(matching.getUserId(), matching.getActivityId(), matching.getPosition());
        matching.setPending(true);
        return ResponseEntity.ok(matching);
    }

    /**
     * API Endpoint that performs a GET request in order to obtain all the activities that a user was mapped to.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of a List of activityID's (which are represented as Longs)
     */
    @GetMapping("/getUserActivities/{userId}")
    public ResponseEntity<List<Long>> getUserActivities(@PathVariable String userId) {
        if(!authManger.getNetId().equals(userId)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(matchingServiceImpl.findActivitiesByUserId(userId));
    }

    /**
     * API Endpoint that performs a POST request in order to let a user unenroll from an activity that he / she
     * was previously assigned to.
     *
     * @param userIdActivityIdPair - Pair object of (userId, activityId) that represents that: the user with 'id' "userID"
     *                             wants to unenroll from activity with 'id' "activityId".
     * @return - ResponseEntity object with a message composed of the previously described Pair object
     */
    @PostMapping("/unenroll")
    public ResponseEntity<Pair<String, Long>> unenroll(@RequestBody Pair<String, Long> userIdActivityIdPair) {
        if(!authManger.getNetId().equals(userIdActivityIdPair.getFirst())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = userIdActivityIdPair.getFirst();
        Long activityId = userIdActivityIdPair.getSecond();
        Optional<Matching> matching = matchingServiceImpl.findMatchingWithPendingFalse(userId, activityId);
        if (matching.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String position = matching.get().getPosition();
        activityPublisher.unenroll(userIdActivityIdPair.getSecond(), position);
        matchingServiceImpl.deleteById(userId, activityId, position);
        return ResponseEntity.ok(userIdActivityIdPair);
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
            @Valid @RequestBody Matching matching
    ) {
        return ResponseEntity.ok(
                matchingServiceImpl.save(matching)
        );
    }

    /**
     * Returns all the timeSlots a user has added in the request body.
     * Mainly for testing purposes of the serializer & deserializer of TimeSlot object
     *
     * @param userId    ID of an User
     * @param timeSlots TimeSlots object
     * @return ResponseEntity with status 200_OK and the List of TimeSlot objects as body
     */
    @PostMapping("/addTimeSlots/{userId}")
    public ResponseEntity<List<TimeSlot>> addTimeSlots(@PathVariable String userId,
                                                       @RequestBody List<TimeSlot> timeSlots) {
        return ResponseEntity.ok(timeSlots);
    }


    /**
     * Handles BAD_REQUEST exceptions thrown by the Validator of Matching entities
     * Acts as a parser of the BAD_REQUEST exception messageb bodies.
     * Triggered for all Matching entities that do not respect the @Valid annotation
     *
     * @param ex exception
     * @return a Mapping fieldName -> errorMessage
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }


}
