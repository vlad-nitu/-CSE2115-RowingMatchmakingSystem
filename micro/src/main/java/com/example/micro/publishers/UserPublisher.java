package com.example.micro.publishers;

import com.example.micro.utils.MatchingUtils;
import com.example.micro.utils.Pair;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserPublisher {

    private final transient MatchingUtils matchingUtils;

    public UserPublisher(MatchingUtils matchingUtils) {
        this.matchingUtils = matchingUtils;
    }

    public UserPublisher() {
        this.matchingUtils = new MatchingUtils("http://localhost:8085/");
    }

    /**
     * Tells the user of the available Activities.
     *
     * @param possibleMatchings a list of possible activities
     */
    public void sendAvailableActivities(List<Pair<Long, String>> possibleMatchings) {
        try {
            matchingUtils.postRequest("/getAvailableActivities", possibleMatchings);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
