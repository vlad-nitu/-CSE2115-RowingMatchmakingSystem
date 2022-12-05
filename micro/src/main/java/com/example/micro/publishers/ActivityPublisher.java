package com.example.micro.publishers;

import com.example.micro.utils.MatchingUtils;
import com.example.micro.utils.TimeSlot;
import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;


@Service
public class ActivityPublisher {

    private static final MatchingUtils matchingUtils = new MatchingUtils("http://localhost:8084/");

    /**
     * obtained timeslots of activityIds
     * @param activityIds
     * @return
     */
    public List<TimeSlot> getTimeSlots(List<Long> activityIds) {
        try {
            Response res = matchingUtils.getRequest("/sendTimeSlots/" + activityIds);
            ArrayList<TimeSlot> timeSlots = res.readEntity(new GenericType<>() {});
            return timeSlots;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Pair<Long, String>> getAvailableActivities(String userId, List<TimeSlot> timeSlots) {
        try {
            Response res = matchingUtils.getRequest("/sendAvailableActivities/" + userId + "/" + timeSlots);
            List<Pair<Long, String>> possibleMatchings = res.readEntity(new GenericType<>() {});
            return possibleMatchings;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    public String getOwnerId(Long activityId) {
        try {
            Response res = matchingUtils.getRequest("/sendOwnerId/" + activityId);
            String ownerId = res.readEntity(new GenericType<>() {});
            return ownerId;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public void takeAvailableSpot(Long activityId, String position) {
        try {
            Pair<Long, String> posTaken = Pair.of(activityId, position);
            Response res = matchingUtils.postRequest("/takeAvailableSpot", posTaken);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
