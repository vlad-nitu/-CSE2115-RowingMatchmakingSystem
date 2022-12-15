package nl.tudelft.cse.sem.template.user.publishers;

import lombok.Cleanup;
import nl.tudelft.cse.sem.template.user.utils.BaseNotification;
import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

@Service
public class NotificationPublisher {

    private final transient UserUtils userUtils;

    public NotificationPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public NotificationPublisher() {
        this.userUtils = new UserUtils("http://localhost:8086/");
    }

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
