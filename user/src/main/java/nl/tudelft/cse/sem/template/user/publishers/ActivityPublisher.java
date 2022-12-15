package nl.tudelft.cse.sem.template.user.publishers;

import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

@Service
public class ActivityPublisher {
    private final transient UserUtils userUtils;

    public ActivityPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public ActivityPublisher() {
        this.userUtils = new UserUtils("http://localhost:8084/");
    }
}
