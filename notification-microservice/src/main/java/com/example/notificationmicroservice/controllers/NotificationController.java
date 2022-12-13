package com.example.notificationmicroservice.controllers;

import com.example.notificationmicroservice.authentication.AuthManager;
import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.services.NotificationDatabaseService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NotificationController {

    private final transient NotificationDatabaseService notificationDatabaseService;
    private final transient AuthManager authManager;

    @Autowired
    public NotificationController(NotificationDatabaseService notificationDatabaseService, AuthManager authManager) {
        this.notificationDatabaseService = notificationDatabaseService;
        this.authManager = authManager;
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
    public ResponseEntity getNotificationsByTarget(@PathVariable String targetId) {
        if (!authManager.getNetId().equals(targetId)) {
            return ResponseEntity.badRequest().body("Only the recipient can ask for their notifications");
        }
        List<Notification> notifications = notificationDatabaseService.findNotificationsByTargetId(targetId);
        notificationDatabaseService.removeNotificationsByTargetId(targetId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/notifyUser")
    public ResponseEntity<Notification> saveNotification(@RequestBody @Valid Notification notification) {
        return ResponseEntity.ok(notificationDatabaseService.save(notification));
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

}
