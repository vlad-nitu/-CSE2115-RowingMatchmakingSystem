package com.example.micro.publishers;

import com.example.micro.utils.BaseNotification;
import com.example.micro.utils.MatchingUtils;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {
    private final transient MatchingUtils matchingUtils;

    public NotificationPublisher(MatchingUtils matchingUtils) {
        this.matchingUtils = matchingUtils;
    }

    public NotificationPublisher() {
        this.matchingUtils = new MatchingUtils("http://localhost:8086/");
    }


    /**
     * Creates a notification for a matching.
     *
     * @param notification the notification that should be sent
     */
    public void notifyUser(BaseNotification notification) {
        try {

            matchingUtils.postRequest("/notifyUser", notification);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
