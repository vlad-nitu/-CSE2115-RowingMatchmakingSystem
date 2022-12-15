package nl.tudelft.cse.sem.template.user.publishers;

import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {

    private final transient UserUtils userUtils;

    public NotificationPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public NotificationPublisher() {
        this.userUtils = new UserUtils("http://localhost:8086/");
    }

}
