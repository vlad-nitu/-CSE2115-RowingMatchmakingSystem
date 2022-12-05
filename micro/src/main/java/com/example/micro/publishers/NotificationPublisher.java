package com.example.micro.publishers;

import com.example.micro.utils.BaseNotification;
import com.example.micro.utils.MatchingUtils;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {
    private static final MatchingUtils matchingUtils = new MatchingUtils("http://localhost:8086/");

    /**
     * Creates a notification for a matching.
     *
     * @param userId the user id of the matching
     * @param targetId the id of the user who should recieve the message
     * @param activityId the activity id of the match
     * @param position the position chosen for the match
     */
    public void notifyUser(String userId, String targetId, Long activityId, String position) {
        try {
            BaseNotification notification = new BaseNotification(userId, targetId, activityId, position);
            Response res = matchingUtils.postRequest("/notifyUser", notification);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
