package nl.tudelft.cse.sem.template.user.controllers;

import nl.tudelft.cse.sem.template.user.authentication.AuthManager;
import nl.tudelft.cse.sem.template.user.publishers.MatchingPublisher;
import nl.tudelft.cse.sem.template.user.services.UserService;
import nl.tudelft.cse.sem.template.user.utils.BaseMatching;
import nl.tudelft.cse.sem.template.user.utils.Pair;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
@RestController
public class UserMatchingController {
    private final transient MatchingPublisher matchingPublisher;
    private final transient AuthManager authManager;
    private final transient UserService userService;
    private static final String noSuchUserIdError = "There is no user with the given userId!";
    private static final String genericPublisherError = "Something went wrong!";


    /**
     * All argument constructor, injects the service for the user database, the authentication manager
     * and the necessary publisher into the controller.
     *
     * @param userService - service for handling the database
     * @param matchingPublisher - a class responsible for sending requests to the matching microservice
     * @param authManager - authentication manager implementation
     */
    public UserMatchingController(MatchingPublisher matchingPublisher, AuthManager authManager, UserService userService) {
        this.matchingPublisher = matchingPublisher;
        this.authManager = authManager;
        this.userService = userService;
    }

    /**
     * API Endpoint that performs a GET request in order to get all the activities that the user can enroll to.
     *
     * @return A list of pairs that contain the activity id and the timeslot in String representation
     *      or the encountered problem description
     */
    @GetMapping("/getAvailableActivities")
    public ResponseEntity getAvailableActivities() throws Exception {
        String userId = authManager.getUserId();
        Set<TimeSlot> timeslots = userService.findTimeSlotsById(userId);
        if (timeslots == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noSuchUserIdError);
        }
        List<Pair<Long, String>> response = matchingPublisher.getAvailableActivities(userId, timeslots);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(genericPublisherError)
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
        String userId = authManager.getUserId();
        BaseMatching response = matchingPublisher.decideMatch(userId, type, matching);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(genericPublisherError)
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * API Endpoint that performs a GET request to get all the activities the User takes part in.
     *
     * @return a list of activity id's or the encountered problem description
     */
    @GetMapping("/getUserActivities")
    public ResponseEntity getUserActivities() throws Exception {
        String userId = authManager.getUserId();
        List<Long> response = matchingPublisher.getUserActivities(userId);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(genericPublisherError)
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * API Endpoint that performs a POST request in order to let the user choose to participate in an activity.
     *
     * @param matching the activity the user wants to participate in
     * @return the matching in which the user has requested to participate or the encountered problem description
     */
    @PostMapping("/chooseActivity")
    public ResponseEntity chooseActivity(@RequestBody BaseMatching matching) throws Exception {
        String userId = authManager.getUserId();
        matching.setUserId(userId);
        BaseMatching response = matchingPublisher.chooseActivity(matching);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(genericPublisherError)
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * API Endpoint that performs a POST request in order to unenroll from an activity.
     *
     * @param activityId the activityId Long attribute where the user wants to unenroll from.
     * @return the userId and activityId pair from the matching that is now cancelled
     *      or the encountered problem description
     */
    @PostMapping("/unenroll")
    public ResponseEntity unenroll(@RequestBody Long activityId) throws Exception {
        String userId = authManager.getUserId();
        Pair<String, Long> userIdActivityIdPair = new Pair<String, Long>(userId, activityId);
        Pair<String, Long> response = matchingPublisher.unenroll(userIdActivityIdPair);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(genericPublisherError)
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
