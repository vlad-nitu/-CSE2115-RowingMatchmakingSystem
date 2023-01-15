package nl.tudelft.cse.sem.template.user.controllers;

import nl.tudelft.cse.sem.template.user.authentication.AuthManager;
import nl.tudelft.cse.sem.template.user.publishers.ActivityPublisher;
import nl.tudelft.cse.sem.template.user.utils.BaseActivity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
@RestController
public class UserActivityController {
    private final transient ActivityPublisher activityPublisher;
    private final transient AuthManager authManager;
    private static final String genericPublisherError = "Something went wrong!";

    /**
     * All argument constructor, injects the main service component, authentication manager
     * and all publishers into the controller.
     *
     * @param activityPublisher - a class responsible for sending requests to the activity microservice
     * @param authManager - authentication manager implementation
     */
    public UserActivityController(ActivityPublisher activityPublisher, AuthManager authManager) {
        this.activityPublisher = activityPublisher;
        this.authManager = authManager;
    }

    /**
     * API Endpoint that performs a GET request in order to cancel an Activity.
     *
     * @param activityId the activityId Long attribute where the user wants to unenroll from.
     * @return a String representing whether Activity deletion was succesful or not
     */
    @GetMapping("/cancelActivity/{activityId}")
    public ResponseEntity<String> cancelActivity(@PathVariable Long activityId) {
        Integer statusCode = activityPublisher.cancelActivity(activityId);
        if (statusCode == HttpStatus.NO_CONTENT.value()) {
            return ResponseEntity.status(HttpStatus.OK).body("Activity was deleted successfully");
        } else {
            return ResponseEntity.status(statusCode).body("Activity deletion was not successful");
        }
    }

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
        if (!type.equals("training") && !type.equals("competition")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Type can only be 'training' or 'competition'.");
        }
        if (!activity.getOwnerId().equals(authManager.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided ownerId does not match your userId! Use "
                    + authManager.getUserId() + " as the ownerId.");
        }
        activity.setType(type);
        BaseActivity response = activityPublisher.createActivity(activity);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(genericPublisherError)
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
