package nl.tudelft.cse.sem.template.user.controllers;

import nl.tudelft.cse.sem.template.user.authentication.AuthManager;
import nl.tudelft.cse.sem.template.user.publishers.NotificationPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
@RestController
public class UserNotificationController {
    private final transient NotificationPublisher notificationPublisher;
    private final transient AuthManager authManager;
    private static final String noSuchUserIdError = "There is no user with the given userId!";
    private static final String genericPublisherError = "Something went wrong!";


    /**
     * All argument constructor, injects the authentication manager
     * and the necessary publisher into the controller.
     *
     * @param notificationPublisher - a class responsible for sending requests to the notification microservice
     * @param authManager - authentication manager implementation
     */
    public UserNotificationController(NotificationPublisher notificationPublisher, AuthManager authManager){
        this.notificationPublisher = notificationPublisher;
        this.authManager = authManager;
    }

    /**
     * API Endpoint that performs a GET request in order to get a list of notifications.
     *
     * @return List of BaseNotification objects representing notifications or the encountered problem description
     */
    @GetMapping("/getNotifications")
    public ResponseEntity getNotifications() throws Exception {
        String userId = authManager.getUserId();
        List<String> response = notificationPublisher.getNotifications(userId);
        return response == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(genericPublisherError)
                : ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
