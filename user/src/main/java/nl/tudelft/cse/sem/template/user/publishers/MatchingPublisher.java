package nl.tudelft.cse.sem.template.user.publishers;

import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

@Service
public class MatchingPublisher {
    private final transient UserUtils userUtils;

    public MatchingPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public MatchingPublisher() {
        this.userUtils = new UserUtils("http://localhost:8083/");
    }
}
