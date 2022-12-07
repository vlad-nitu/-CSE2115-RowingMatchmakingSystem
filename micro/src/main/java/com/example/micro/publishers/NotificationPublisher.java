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
     * @param userId the user id of the matching
     * @param targetId the id of the user who should recieve the message
     * @param activityId the activity id of the match
     * @param position the position chosen for the match
     */
    public void notifyUser(String userId, String targetId, Long activityId, String position, String type) {
        try {
            BaseNotification notification = new BaseNotification(userId, targetId, activityId, position, type);
            matchingUtils.postRequest("/notifyUser", notification);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
