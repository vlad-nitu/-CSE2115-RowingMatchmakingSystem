package com.example.notificationmicroservice.controllers;

import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.services.NotificationDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
