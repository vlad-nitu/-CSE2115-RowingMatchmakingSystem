package com.example.micro.publishers;

import com.example.micro.utils.MatchingUtils;
import com.example.micro.utils.TimeSlot;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class UserPublisher {

    private static final MatchingUtils matchingUtils = new MatchingUtils("http://localhost:8085/");

    public void sendAvailableActivities(List<Pair<Long, String>> possibleMatchings) {
        try {
            Response res = matchingUtils.postRequest("/getAvailableActivities", possibleMatchings);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
