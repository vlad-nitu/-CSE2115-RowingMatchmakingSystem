package com.example.notificationmicroservice.controllers;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NotificationController {

    private final transient NotificationDatabaseService notificationDatabaseService;
    //private final transient AuthService

    @Autowired
    public NotificationController(NotificationDatabaseService notificationDatabaseService) {
        this.notificationDatabaseService = notificationDatabaseService;
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
    public ResponseEntity<List<Notification>> getNotificationsByTarget(@PathVariable String targetId) {
        List<Notification> notifications = notificationDatabaseService.findNotificationsByTargetId(targetId);
        notificationDatabaseService.removeNotificationsByTargetId(targetId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/notifyUser")
    public ResponseEntity<Notification> saveNotification(@RequestBody Notification notification) {
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
