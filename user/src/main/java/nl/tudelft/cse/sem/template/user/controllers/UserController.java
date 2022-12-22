package nl.tudelft.cse.sem.template.user.controllers;

import nl.tudelft.cse.sem.template.user.authentication.AuthManager;
import nl.tudelft.cse.sem.template.user.publishers.ActivityPublisher;
import nl.tudelft.cse.sem.template.user.publishers.MatchingPublisher;
import nl.tudelft.cse.sem.template.user.publishers.NotificationPublisher;
import nl.tudelft.cse.sem.template.user.utils.InputValidation;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.services.UserService;

import javax.validation.Valid;
import java.util.*;

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
     * API Endpoint that performs a GET request in order to obtain and send the timeslots of a User.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a Set of Timeslot objects representing the timeslots
     */
    @GetMapping("/sendTimeSlots/{userId}")
    public ResponseEntity<Set<TimeSlot>> sendTimeSlots(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findTimeSlotsById(userId));
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

