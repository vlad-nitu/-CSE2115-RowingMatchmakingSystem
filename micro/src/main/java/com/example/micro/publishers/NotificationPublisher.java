package com.example.micro.publishers;

import com.example.micro.utils.BaseNotification;
import com.example.micro.utils.MatchingUtils;
import java.util.List;
import jakarta.ws.rs.core.Response;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {
    private static final MatchingUtils matchingUtils = new MatchingUtils("http://localhost:8086/");

    public void notifyUser(String userId, String targetId, Long activityId, String position) {
        try {
            BaseNotification notification = new BaseNotification(userId, targetId, activityId, position);
            Response res = matchingUtils.postRequest("/notifyUser", notification);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
