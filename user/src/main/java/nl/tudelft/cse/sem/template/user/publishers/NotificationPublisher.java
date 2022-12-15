package nl.tudelft.cse.sem.template.user.publishers;

import lombok.Cleanup;
import lombok.Generated;
import nl.tudelft.cse.sem.template.user.utils.BaseNotification;
import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

@Service
@Generated
public class NotificationPublisher {

    private final transient UserUtils userUtils;

    public NotificationPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public NotificationPublisher() {
        this.userUtils = new UserUtils("http://localhost:8086/");
    }

    // do we need a list of notifications here?
    /**
     * Requests all the notifications that are collected for the user and have not been seen yet.
     *
     * @param userId the id of the user requesting the notifications
     * @return the received notification, if any are present
     */
    public BaseNotification getNotifications(String userId) {
        try {
            @Cleanup
            Response res = userUtils.getRequest("/getNotifications/" + userId);
            BaseNotification notification = res.readEntity(new GenericType<>() {});
            return notification;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new BaseNotification();
        }
    }

}
