package com.example.micro.publishers;

import com.example.micro.utils.MatchingUtils;
import com.example.micro.utils.Pair;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserPublisher {

    private static transient MatchingUtils matchingUtils = new MatchingUtils("http://localhost:8085/");

    public UserPublisher() {
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
