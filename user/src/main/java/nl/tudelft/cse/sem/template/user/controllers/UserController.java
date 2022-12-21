package nl.tudelft.cse.sem.template.user.controllers;

import com.fasterxml.jackson.databind.ser.Serializers;
import nl.tudelft.cse.sem.template.user.authentication.AuthManager;
import nl.tudelft.cse.sem.template.user.publishers.ActivityPublisher;
import nl.tudelft.cse.sem.template.user.publishers.MatchingPublisher;
import nl.tudelft.cse.sem.template.user.publishers.NotificationPublisher;
import nl.tudelft.cse.sem.template.user.utils.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.services.UserService;

import javax.validation.Valid;
import javax.ws.rs.core.Response;
import java.util.*;

@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
@RestController
public class UserController {

    private final transient UserService userService;
    private final transient ActivityPublisher activityPublisher;
    private final transient MatchingPublisher matchingPublisher;
    private final transient NotificationPublisher notificationPublisher;
    private final transient AuthManager authManager;

    private static final String noSuchUserIdError = "There is no user with the given userId!";

    /**
     * All argument constructor, injects the main service component, authentication manager
     * and all publishers into the controller.
     *
     * @param userService - user service implementation
     * @param authManager - authentication manager implementation
     */
    public UserController(UserService userService, ActivityPublisher activityPublisher, MatchingPublisher matchingPublisher,
                          NotificationPublisher notificationPublisher, AuthManager authManager) {
        this.userService = userService;
        this.activityPublisher = activityPublisher;
        this.matchingPublisher = matchingPublisher;
        this.notificationPublisher = notificationPublisher;
        this.authManager = authManager;
    }

    /**
     * API Endpoint that performs a POST request in order to save a new User to the database.
     *
     * @param user the User that needs to be added
     * @return ResponseEntity object with a message composed of the User that was added
     *      or the encountered problem description
     */
    @PostMapping("/createUser")
    public ResponseEntity createUser(@Valid @RequestBody User user) {
        if (!InputValidation.userIdValidation(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided ID is invalid!");
        }
        if (!InputValidation.userGenderValidation(user.getGender())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided gender is invalid!");
        }
        if (!InputValidation.validatePositions(user.getPositions())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("One of the positions that you provided is not valid!");
        }
        if (!user.getUserId().equals(authManager.getNetId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided userId does not match your netId! Use "
                    + authManager.getNetId() + " as the userId.");
        }
        Optional<User> foundUser = userService.findUserById(user.getUserId());
        if (foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with the given ID already exists!");
        }
        return ResponseEntity.ok(userService.save(user));
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the competitiveness of the User.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of the competitiveness
     *      (which is a boolean, either true for competitive or false for non-competitive users)
     *      or the encountered problem description
     */
    @GetMapping("/sendCompetitiveness/{userId}")
    public ResponseEntity sendCompetitiveness(@PathVariable String userId) {
        String responseBody = userService.findCompetitivenessByUserId(userId);
        if (responseBody.equals("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noSuchUserIdError);
        }
        return responseBody.equals("true") ? ResponseEntity.ok(true) : ResponseEntity.ok(false);
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the gender of the User.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of the gender of the User (a character, F/M)
     *      or the encountered problem description
     */
    @GetMapping("/sendGender/{userId}")
    public ResponseEntity sendGender(@PathVariable String userId) {
        Character responseBody = userService.findGenderById(userId);
        return responseBody == ' ' ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                noSuchUserIdError) : ResponseEntity.ok(responseBody);
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the cox-certificate of the User.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of the certificate ('none' if no certificate is entered)
     *      or the encountered problem description
     */
    @GetMapping("/sendCertificate/{userId}")
    public ResponseEntity sendCertificate(@PathVariable String userId) {
        String responseBody = userService.findCertificateById(userId);
        return responseBody == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                noSuchUserIdError) : ResponseEntity.ok(responseBody);
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the organization of the User.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of the organization
     *      or the encountered problem description
     */
    @GetMapping("/sendOrganization/{userId}")
    public ResponseEntity sendOrganization(@PathVariable String userId) {
        String responseBody = userService.findOrganisationById(userId);
        return responseBody == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                noSuchUserIdError) : ResponseEntity.ok(responseBody);
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the positions the User can fulfil.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of a Set of strings which represent the positions
     *      the User can take in a rowing boat or the encountered problem description
     */
    @GetMapping("/sendPositions/{userId}")
    public ResponseEntity sendPositions(@PathVariable String userId) {
        Set<String> responseBody = userService.findPositionsById(userId);
        return responseBody == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                noSuchUserIdError) : ResponseEntity.ok(responseBody);
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the timeslots the User selected.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of a Set of TimeSlots which represent the timeslots
     *      the User specified or the encountered problem description
     */
    @GetMapping("/sendTimeSlots/{userId}")
    public ResponseEntity sendTimeSlots(@PathVariable String userId) {
        Set<TimeSlot> responseBody = userService.findTimeSlotsById(userId);
        return responseBody == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                noSuchUserIdError) : ResponseEntity.ok(responseBody);
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the user's e-mail address.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of the e-mail address
     *      or the encountered problem description
     */
    @GetMapping("/sendEmail/{userId}")
    public ResponseEntity sendEmail(@PathVariable String userId) {
        String responseBody = userService.findEmailById(userId);
        return responseBody == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                noSuchUserIdError) : ResponseEntity.ok(responseBody);
    }

    /**
     * Find all users.
     *
     * @return - Response of a list of users
     */
    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // TODO: debug, write tests
    /**
     * API Endpoint that performs a POST request in order to create an activity.
     *
     * @param activity - BaseActivity representing the activity
     * @return ResponseEntity object with status OK or INTERNAL_SERVER_ERROR
     *      and descriptive body
     * @throws Exception - caught exception
     */
    @PostMapping("/createActivity/{type}")
    public ResponseEntity createActivity(@Valid @RequestBody BaseActivity activity,
                                         @PathVariable String type) throws Exception {
        if (!activity.getOwnerId().equals(authManager.getNetId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided ownerId does not match your netId! Use "
                    + authManager.getNetId() + " as the ownerId.");
        }
        BaseActivity response = activityPublisher.createActivity(activity);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong!")
                : ResponseEntity.status(HttpStatus.OK).body(response);
        /* try {
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong.");
        }*/
    }

    /**
     * API Endpoint that performs a GET request in order to get all the activities that the user can enroll to.
     *
     * @return A list of pairs that contain the activity id and the timeslot in String representation
     *      or the encountered problem description
     */
    @GetMapping("/getAvailableActivities")
    public ResponseEntity getAvailableActivities() throws Exception {
        String userId = authManager.getNetId();
        Set<TimeSlot> timeslots = userService.findTimeSlotsById(userId);
        if (timeslots == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noSuchUserIdError);
        }
        List<Pair<Long, String>> response = matchingPublisher.getAvailableActivities(userId, timeslots);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong!")
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * API Endpoint that performs a GET request in order to get a list of notifications.
     *
     * @return List of BaseNotification objects representing notifications or the encountered problem description
     */
    @GetMapping("/getNotifications")
    public ResponseEntity getNotifications() throws Exception {
        String userId = authManager.getNetId();
        List<String> response = notificationPublisher.getNotifications(userId);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong!")
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * API Endpoint that performs a POST request in order to decide whether another User is accepted as a
     * match or declined (by the owner of the activity).
     *
     * @param type - either 'accept' or 'decline'
     * @param matching - the matching that is accepted or declined
     * @return the match if the decision was successful or the encountered problem description
     */
    @PostMapping("/decideMatch/{type}")
    public ResponseEntity decideMatch(@PathVariable String type, @RequestBody BaseMatching matching) throws Exception {
        if (!type.equals("accept") && !type.equals("decline")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Decision can only be 'accept' or 'decline'.");
        }
        String userId = authManager.getNetId();
        BaseMatching response = matchingPublisher.decideMatch(userId, type, matching);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong!")
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * API Endpoint that performs a GET request to get all the activities the User takes part in.
     *
     * @return a list of activity id's or the encountered problem description
     */
    @GetMapping("/getUserActivities")
    public ResponseEntity getUserActivities() throws Exception {
        String userId = authManager.getNetId();
        List<Long> response = matchingPublisher.getUserActivities(userId);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong!")
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * API Endpoint that performs a POST request in order to let the user choose to participate in an activity.
     *
     * @param matching the activity the user wants to participate in
     * @return the matching in which the user has requested to participate
     */
    @PostMapping("/chooseActivity")
    public ResponseEntity chooseActivity(@RequestBody BaseMatching matching) throws Exception {
        String userId = authManager.getNetId();
        matching.setUserId(userId);
        BaseMatching response = matchingPublisher.chooseActivity(matching);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong!")
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * API Endpoint that performs a POST request in order to unenroll from an activity.
     *
     * @param activity the activity where the user wants to unenroll from.
     * @return the userId and activityId pair from the matching that is now cancelled
     */
    @PostMapping("/unenroll")
    public ResponseEntity unenroll(@RequestBody BaseActivity activity) throws Exception {
        String userId = authManager.getNetId();
        Long activityId = activity.getActivityId();
        Pair<String, Long> userIdActivityIdPair = new Pair<String, Long>(userId, activityId);
        Pair<String, Long> response = matchingPublisher.unenroll(userIdActivityIdPair);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong!")
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Handles BAD_REQUEST exceptions thrown by the Validator of User entities
     * Acts as a parser of the BAD_REQUEST exception messages bodies.
     * Triggered for all User entities that do not respect the @Valid annotation
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

