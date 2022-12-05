package com.example.micro.publishers;

import com.example.micro.utils.MatchingUtils;
import java.util.List;
import javax.ws.rs.core.Response;
import org.springframework.data.util.Pair;
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
            Response res = matchingUtils.postRequest("/getAvailableActivities", possibleMatchings);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
