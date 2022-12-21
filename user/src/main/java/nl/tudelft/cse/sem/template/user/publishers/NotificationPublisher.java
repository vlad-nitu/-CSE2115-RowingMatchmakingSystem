package nl.tudelft.cse.sem.template.user.publishers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.Generated;
import nl.tudelft.cse.sem.template.user.utils.BaseNotification;
import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Service
@Generated
@SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
public class NotificationPublisher {

    private final transient UserUtils userUtils;

    public NotificationPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public NotificationPublisher() {
        this.userUtils = new UserUtils("http://localhost:8086/");
    }

    /**
     * Requests all the notifications that are collected for the user and have not been seen yet.
     *
     * @param userId the id of the user requesting the notifications
     * @return a list containing the received notifications or null if an error was encountered
     */
    public List<String> getNotifications(String userId) throws Exception {
        @Cleanup
        Response res = userUtils.getRequest("/getNotifications/" + userId);
        return res.getStatus() == 200 ? res.readEntity(new GenericType<List<String>>(){}) : null;
    }

}
