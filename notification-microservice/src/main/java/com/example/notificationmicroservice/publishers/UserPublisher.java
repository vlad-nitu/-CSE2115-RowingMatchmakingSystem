package com.example.notificationmicroservice.publishers;

import com.example.notificationmicroservice.utils.NotificationUtils;
import lombok.Cleanup;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

@Service
public class UserPublisher {

    private final transient NotificationUtils notificationUtils;

    UserPublisher(NotificationUtils notificationUtils) {
        this.notificationUtils = notificationUtils;
    }

    UserPublisher() {
        this.notificationUtils = new NotificationUtils("http://localhost:8085/");
    }

    /**
     * Request the email of the user.
     *
     * @param userId the id of the user
     * @return the email of the user
     */
    public String getEmail(String userId) {
        try {
            @Cleanup
            Response res = notificationUtils.getRequest("/sendEmail/" + userId);
            return res.readEntity(new GenericType<>() {});
        } catch (Exception e) {
            System.out.println("Bad request");
            return null;
        }
    }
}
