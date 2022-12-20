package nl.tudelft.cse.sem.template.user.controllers;

import nl.tudelft.cse.sem.template.user.publishers.ActivityPublisher;
import nl.tudelft.cse.sem.template.user.publishers.MatchingPublisher;
import nl.tudelft.cse.sem.template.user.publishers.NotificationPublisher;
import nl.tudelft.cse.sem.template.user.utils.InputValidation;
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

//TODO parse or personalise error messages
@RestController
public class UserController {

    private final transient UserService userService;
    private final transient ActivityPublisher activityPublisher;
    private final transient MatchingPublisher matchingPublisher;
    private final transient NotificationPublisher notificationPublisher;

    /**
     * All argument constructor, injects the main service component and all (4 now 0) publishers into the controller.
     *
     * @param userService - user service implementation
     */
    public UserController(UserService userService, ActivityPublisher activityPublisher,
                          MatchingPublisher matchingPublisher, NotificationPublisher notificationPublisher) {
        this.userService = userService;
        this.activityPublisher = activityPublisher;
        this.matchingPublisher = matchingPublisher;
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * API Endpoint that performs a POST request in order to save a new User to the database.
     *
     * @param user the User that needs to be added
     * @return ResponseEntity object with a message composed of the User that was added
     */
    @PostMapping("/createUser")
    public ResponseEntity createUser(@Valid @RequestBody User user) {
        if (!InputValidation.userIdValidation(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided ID is invalid!");
        }
        if (!InputValidation.userGenderValidation(user.getGender())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided gender is invalid!");
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
     * @return - ResponseEntity object with a message composed of the competitiveness (which is a boolean,
     *         either true for competitive or false for non-competitive users)
     */
    @GetMapping("/sendCompetitiveness/{userId}")
    public ResponseEntity<Boolean> sendCompetitiveness(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findCompetitivenessByUserId(userId));
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the gender of the User.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of the gender of the User (a character, F/M)
     */
    @GetMapping("/sendGender/{userId}")
    public ResponseEntity<Character> sendGender(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findGenderById(userId));
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the cox-certificate of the User.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of the certificate ('none' if no certificate is entered)
     */
    @GetMapping("/sendCertificate/{userId}")
    public ResponseEntity<String> sendCertificate(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findCertificateById(userId));
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the organization of the User.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of the organization
     */
    @GetMapping("/sendOrganization/{userId}")
    public ResponseEntity<String> sendOrganization(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findOrganisationById(userId));
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the positions the User can fulfil.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of a Set of strings which represent the positions
     *          the User can take in a rowing boat
     */
    @GetMapping("/sendPositions/{userId}")
    public ResponseEntity<Set<String>> sendPositions(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findPositionsById(userId));
    }

    /**
     * API Endpoint that performs a GET request in order to obtain and send the user's e-mail address.
     *
     * @param userId - String object representing the unique identifier of a User
     * @return - ResponseEntity object with a message composed of the e-mail address
     */
    @GetMapping("/sendEmail/{userId}")
    public ResponseEntity<String> sendEmail(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findEmailById(userId));
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

