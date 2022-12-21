package com.example.notificationmicroservice.controllers;

import com.example.notificationmicroservice.authentication.AuthManager;
import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.services.NotificationDatabaseService;
import com.example.notificationmicroservice.strategy.NotificationStrategy;
import com.example.notificationmicroservice.utils.InputValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NotificationController {

    private final transient NotificationDatabaseService notificationDatabaseService;
    private final transient AuthManager authManager;
    private transient NotificationStrategy strategy;

    /** Notification controller for injection.
     *
     * @param notificationDatabaseService service that communicates with the notification database
     * @param authManager authentication manager
     */
    @Autowired
    public NotificationController(NotificationDatabaseService notificationDatabaseService,
                                  AuthManager authManager, NotificationStrategy strategy) {
        this.notificationDatabaseService = notificationDatabaseService;
        this.authManager = authManager;
        this.strategy = strategy;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> getNotificationsByTarget() {
        return ResponseEntity.ok("notifications");
    }

    /**
     * Notification endpoint for users.
     *
     * @param targetId the requesting user that wants to get notifications
     * @return notifications addressed to the user
     */
    @GetMapping("/getNotifications/{targetId}")
    public ResponseEntity<List<String>> getNotificationsByTarget(@PathVariable String targetId) {
        if (!authManager.getNetId().equals(targetId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Notification> notifications = notificationDatabaseService.findNotificationsByTargetId(targetId);
        List<String> notificationMessages = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationMessages.add(notification.buildMessage());
        }
        notificationDatabaseService.removeNotificationsByTargetId(targetId);
        return ResponseEntity.ok(notificationMessages);
    }

    /** Handles the incoming request to notify user with current selected strategy.
     *
     * @param notification notification that is to be handled by notification strategy
     * @return if it succeeded to be handled by notification strategy
     */
    @PostMapping("/notifyUser")
    public ResponseEntity<String> notifyUser(@RequestBody @Valid Notification notification) {
        if(!InputValidation.validatePosition(notification.getPosition())) {
            return ResponseEntity.badRequest().body("Invalid position");
        }
        if(!InputValidation.validateType(notification.getType())) {
            return ResponseEntity.badRequest().body("Invalid notification type");
        }
        if (!strategy.handleNotification(notification)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(strategy.getFailureMessage());
        }

        return ResponseEntity.ok("Successfully notified");
    }

    /**
     * Concatenates all invalid parameter texts.
     *
     * @param ex exceptions
     * @return invalid parameter exception texts.
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

    public void setNotificationStrategy(NotificationStrategy notificationStrategy) {
        this.strategy = notificationStrategy;
    }
}
