

package com.example.activitymicroservice.controllers;

import com.example.activitymicroservice.authentication.AuthManager;
import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.MatchingPublisher;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.services.ActivityService;
import com.example.activitymicroservice.utils.InputValidation;
import com.example.activitymicroservice.utils.Pair;
import com.example.activitymicroservice.utils.TimeSlot;
import com.example.activitymicroservice.validators.Validator;
import com.example.activitymicroservice.validators.GenderValidator;
import com.example.activitymicroservice.validators.PositionValidator;
import com.example.activitymicroservice.validators.OrganisationValidator;
import com.example.activitymicroservice.validators.CertificateValidator;
import com.example.activitymicroservice.validators.CompetitivenessValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.InvalidObjectException;
import java.util.*;

@RestController
public class ActivityController {
    private final transient ActivityService activityService;
    private final transient UserPublisher userPublisher;
    private final transient MatchingPublisher matchingPublisher;
    private final transient AuthManager authManager;

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
    }

    @GetMapping("/sendOwnerId/{activityId}")
    public ResponseEntity<String> sendOwnerId(@PathVariable Long activityId) {
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
    public ResponseEntity takeAvailableSpot(@RequestBody @Valid Pair<Long, String> posTaken) {
        try {
            if (!InputValidation.validatePosition(posTaken.getSecond())) {
                return ResponseEntity.badRequest().body("Invalid Position");
            }
            activityService.takeSpot(posTaken);
            return ResponseEntity.ok().build();
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
        Activity activity = this.activityService.findActivity(activityId);
        Character gender = this.userPublisher.getGender(userId);
        boolean competitiveness = this.userPublisher.getCompetitiveness(userId);
        String organisation = this.userPublisher.getOrganisation(userId);
        String certificate = this.userPublisher.getCertificate(userId);
        List<String> listPositions = this.userPublisher.getPositions(userId);
        return ResponseEntity.ok(this.activityService.checkUser(activity, gender,
                certificate, organisation, competitiveness, listPositions, position));
    }

    /**
     * API enpoint that allows for creating an Activity object and persisting it to the DB.
     *
     * @param activity - Activity object that you will create
     * @return - 200_OK, if activity was created, or 400_BAD_REQUEST if the Activity object failed input validation stage
     */
    @PostMapping("/createActivity")
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {

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
                                                                            @PathVariable String userId)
            throws InvalidObjectException {
        List<Pair<Long, String>> list = new ArrayList<>();
        Validator positionValidator = new PositionValidator();

        Validator certificateValidator = new CertificateValidator();
        certificateValidator.setNext(positionValidator);

        Validator genderValidator = new GenderValidator();
        genderValidator.setNext(certificateValidator);

        Validator organizationValidator = new OrganisationValidator();
        organizationValidator.setNext(genderValidator);

        Validator competitivenessValidator = new CompetitivenessValidator();
        competitivenessValidator.setNext(organizationValidator);

        List<Activity> activityList = activityService.getActivitiesByTimeSlot(timeSlots);
        for (Activity activity : activityList) {
            for (String position : activity.getPositions()) {
                boolean isValid = competitivenessValidator.handle(activity, userPublisher, position, userId);
                if (isValid) {
                    list.add(new Pair<>(activity.getActivityId(), position));
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
        if (!authManager.getNetId().equals(activity.get().getOwnerId())) {
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




