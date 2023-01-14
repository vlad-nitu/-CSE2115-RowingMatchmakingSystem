

package com.example.activitymicroservice.controllers;

import com.example.activitymicroservice.authentication.AuthManager;
import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.domain.Training;
import com.example.activitymicroservice.publishers.MatchingPublisher;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.services.ActivityService;
import com.example.activitymicroservice.utils.InputValidation;
import com.example.activitymicroservice.utils.Pair;
import com.example.activitymicroservice.utils.TimeSlot;
import com.example.activitymicroservice.validators.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.InvalidObjectException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class ActivityController {
    private final transient ActivityService activityService;
    private final transient UserPublisher userPublisher;
    private final transient MatchingPublisher matchingPublisher;
    private final transient AuthManager authManager;
    private final transient Validator training;
    private final transient Validator trainingCox;
    private final transient Validator competition;
    private final transient Validator competitionCox;

    /**
     * Constructor for injection.
     *
     * @param activityService   activity service
     * @param userPublisher     user publisher
     * @param matchingPublisher matching publisher
     * @param authManager       authentication manager
     */
    public ActivityController(ActivityService activityService,
                              UserPublisher userPublisher, MatchingPublisher matchingPublisher, AuthManager authManager) {
        this.activityService = activityService;
        this.userPublisher = userPublisher;
        this.matchingPublisher = matchingPublisher;
        this.authManager = authManager;

        training = new PositionValidator();

        trainingCox = new CertificateValidator();
        trainingCox.setNext(training);

        Validator organizationValidator = new OrganisationValidator();
        organizationValidator.setNext(training);

        Validator competitivenessValidator = new CompetitivenessValidator();
        competitivenessValidator.setNext(organizationValidator);

        competition = new GenderValidator();
        competition.setNext(competitivenessValidator);

        competitionCox = new CertificateValidator();
        competitionCox.setNext(competition);

    }

    /**
     * API Endpoint that performs a Get Request to obtain the Owner ID of a given activity.
     *
     * @param activityId Long object that represents an Activity's ID
     * @return a String object representing the Owner ID
     */
    @GetMapping("/sendOwnerId/{activityId}")
    public ResponseEntity<String> sendOwnerId(@PathVariable Long activityId) {
        if (!activityService.findActivityOptional(activityId).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(activityService.findActivityOptional(activityId).get().getOwnerId());
    }

    /**
     * API Endpoint that performs a Post Request to return a List of
     * TimeSlots representing the TimeSlots of a list of Activities.
     *
     * @param activityIds List Object representing the list of Activities
     * @return a List containing the TimeSlots of those Activities.
     */
    @PostMapping("/sendTimeSlots")
    public ResponseEntity<List<TimeSlot>> sendTimeSlots(@RequestBody List<Long> activityIds) {
        try {
            return ResponseEntity.ok(this.activityService.getTimeSlotsByActivityIds(activityIds));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
    public ResponseEntity<String> takeAvailableSpot(@RequestBody @Valid Pair<Long, String> posTaken) {
        try {
            if (!InputValidation.validatePosition(posTaken.getSecond())) {
                return ResponseEntity.badRequest().body("Invalid Position");
            }
            activityService.takeSpot(posTaken);
            return ResponseEntity.ok(posTaken.getSecond());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * API Endpoint that performs a GET request in order to
     * check if a User is eligible for a certain Activity.
     *
     * @param userId     String object representing the User's ID
     * @param activityId Long object representing the Activity's ID
     * @param position   String object representing the position the User is applying to
     * @return ResponseEntity object with a boolean to certify if the User is eligible for that Activity or not
     */
    @GetMapping("/check/{userId}/{activityId}/{position}")
    public ResponseEntity<Boolean> check(@PathVariable String userId,
                                         @PathVariable Long activityId, @PathVariable String position) {

        Optional<Activity> activityOptional = this.activityService.findActivityOptional(activityId);
        if (!activityOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Activity activity = activityOptional.get();
        if (!activity.getPositions().contains(position)) {
            return ResponseEntity.ok(false);
        }
        List<Activity> activities = activityService.getActivitiesByTimeSlot(List.of(activity),
                userPublisher.getTimeslots(userId));
        if (activities.isEmpty()) {
            return ResponseEntity.ok(false);
        }

        // here we use a helper method "getValidator" to lower the cyclomatic complexitiy of this method
        Validator validator = getValidator(activity, position);
        try {
            boolean isValid = validator.handle(activity, userPublisher, position, userId);
            return ResponseEntity.ok(isValid);
        } catch (InvalidObjectException e) {
            return ResponseEntity.ok(false);
        }
    }

    private Validator getValidator(Activity activity, String position) {
        // This refactor uses a Map to store the validators for different activity types and positions,
        // with keys that are constructed from the activity type and position.
        Map<String, Validator> validators = new HashMap<>();
        validators.put("competitioncox", competitionCox);
        validators.put("competition", competition);
        validators.put("trainingcox", trainingCox);
        validators.put("training", training);
        // The getValidator method creates the key by getting the simple name of the activity class in lowercase
        // and concatenating it with the position.
        String key = activity.getClass().getSimpleName().toLowerCase(Locale.US);
        if (position.equals("cox")) {
            key += position;
        }
        System.out.println(key);
        return validators.getOrDefault(key, training);
    }



    /**
     * API enpoint that allows for creating an Activity object and persisting it to the DB.
     *
     * @param activity - Activity object that you will create
     * @return - 200_OK, if activity was created, or 400_BAD_REQUEST if the Activity object failed input validation stage
     */
    @PostMapping("/createActivity")
    public ResponseEntity<Activity> createActivity(@Valid @RequestBody Activity activity) {

        if (InputValidation.validatePositions(activity.getPositions())) {
            return ResponseEntity.ok(this.activityService.save(activity));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * API Endpoint that performs a POST request in order to return all
     * Pair(Activity.ID, Position) that a certain User can enrol to.
     *
     * @param timeSlots the List of TimeSlots of a User
     * @param userId    the ID of the User
     * @return a List of Pair(Activity.ID, Position)
     */
    @PostMapping("/sendAvailableActivities/{userId}")
    public ResponseEntity<List<Pair<Long, String>>> sendAvailableActivities(@RequestBody List<TimeSlot> timeSlots,
                                                                            @PathVariable String userId) {
        List<Pair<Long, String>> list = new ArrayList<>();


        List<Activity> activityList =
                activityService.getActivitiesByTimeSlot(activityService.findAll(), timeSlots);
        for (Activity activity : activityList) {
            for (String position : activity.getPositions()) {
                Validator validator;
                if (activity instanceof Competition && position.equals("cox")) {
                    validator = competitionCox;
                } else if (activity instanceof Competition) {
                    validator = competition;
                } else if (activity instanceof Training && position.equals("cox")) {
                    validator = trainingCox;
                } else {
                    validator = training;
                }
                try {
                    validator.handle(activity, userPublisher, position, userId);
                    list.add(new Pair<>(activity.getActivityId(), position));
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return ResponseEntity.ok(list);
    }


    /**
     * API endpoint that performs a DELETE request for the given activityId.
     *
     * @param activityId the id of the activity to be deleted
     * @return the deleted activity
     */
    @PostMapping("/cancelActivity/{activityId}")
    public ResponseEntity<Activity> cancelActivity(@PathVariable Long activityId) {
        Optional<Activity> activity = activityService.findActivityOptional(activityId);
        if (activity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!authManager.getUserId().equals(activity.get().getOwnerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!matchingPublisher.deleteMatchingByActivityId(activityId)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        activityService.deleteById(activityId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(activity.get());
    }



    /**
     * API Endpoint that receives a POST request and opens up a position
     * that was taken before.
     *
     * @param posTaken Pair of a Long and String object representing the ID of the activity and the
     *                 position occupied respectively
     * @return ResponseEntity object that specifies if the request could be done
     */
    @PostMapping("/unenrollPosition")
    public ResponseEntity unenrollPosition(@RequestBody @Valid Pair<Long, String> posTaken) {
        if (!InputValidation.validatePosition(posTaken.getSecond())) {
            return ResponseEntity.badRequest().body("Invalid position");
        }
        try {
            activityService.unenrollPosition(posTaken);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Find all activities.
     *
     * @return - Response of a list of activities
     */
    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Activity>> findAllUsers() {
        return ResponseEntity.ok(activityService.findAll());
    }

    /**
     * API Endpoint that lets an owner edit an Activity.
     *
     * @param activityId the ID of the Activity
     * @param newActivity the new format of the edited Activity
     * @return a Response Entity containing the newly edited Activity
     */
    @PostMapping("/editActivity/{activityId}")
    public ResponseEntity<Activity> editActivity(@PathVariable Long activityId,
                                                 @Valid @RequestBody Activity newActivity) {
        Optional<Activity> activity = activityService.findActivityOptional(activityId);
        if (activity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!authManager.getUserId().equals(activity.get().getOwnerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!matchingPublisher.deleteMatchingByActivityId(activityId)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (!activityService.editActivityService(activity.get(), newActivity)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(activityService.findActivityOptional(activityId).get());
    }

    /**
     * Handles BAD_REQUEST exceptions thrown by the Validator of Activity entities
     * Acts as a parser of the BAD_REQUEST exception messageb bodies.
     *
     * @param ex exception
     * @return a Mapping fieldName -> errorMessage
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }


}




