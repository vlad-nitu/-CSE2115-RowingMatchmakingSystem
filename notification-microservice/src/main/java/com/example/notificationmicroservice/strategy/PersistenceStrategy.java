package com.example.notificationmicroservice.strategy;

import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.services.NotificationDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersistenceStrategy implements NotificationStrategy {

    private final transient NotificationDatabaseService notificationDatabaseService;

    @Autowired
    public PersistenceStrategy(NotificationDatabaseService notificationDatabaseService) {
        this.notificationDatabaseService = notificationDatabaseService;
    }

    /** Persist the notification so that a user could later request it.
     *
     * @param notification notification to be stored to database
     * @return if it was successfully stored in the database
     */
    @Override
    public boolean handleNotification(Notification notification) {
        return notificationDatabaseService.save(notification) != null;
    }

    /** A failure message if handleNotification fails.
     *
     * @return the message
     */
    @Override
    public String getFailureMessage() {
        return "Notification failed to be saved to database for later user requests";
    }
}
