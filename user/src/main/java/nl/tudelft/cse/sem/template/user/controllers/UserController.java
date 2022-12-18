package nl.tudelft.cse.sem.template.user.controllers;

import nl.tudelft.cse.sem.template.user.utils.InputValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.services.UserService;

import java.util.Optional;
import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    private final transient UserService userService;

    /**
     * All argument constructor, injects the main service component and all (4 now 0) publishers into the controller.
     *
     * @param userService - user service implementation
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * API Endpoint that performs a POST request in order to save a new User to the database.
     *
     * @param user the User that needs to be added
     * @return ResponseEntity object with a message composed of the User that was added
     */
    @PostMapping("/createUser")
    public ResponseEntity createUser(@RequestBody User user) {
        if (!InputValidation.userIdValidation(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided ID is invalid!");
            // return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<User> foundUser = userService.findUserById(user.getUserId());
        if (foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with the given ID already exists!");
            // return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
     * Find all users.
     *
     * @return - Response of a list of users
     */
    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
}
