package com.example.micro.publishers;

import com.example.micro.utils.MatchingUtils;
import com.example.micro.utils.TimeSlot;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;




@Service
public class ActivityPublisher {

    private static final MatchingUtils matchingUtils = new MatchingUtils("http://localhost:8084/");

    public ActivityPublisher() {
    }

    /**
     * Obtained timeslots of all activityIds.
     *
     * @param activityIds activities of the id
     * @return List of time slots
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

    /**
     * Request the available matches based on the user id and time slots.
     *
     * @param userId The id of the user
     * @param timeSlots The time slots that the user is available in
     * @return a list of possible matches
     */
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

    /**
     * Request the owner of the activity.
     *
     * @param activityId the id of the activity
     * @return the id of the owner of specified activity
     */
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

    /**
     * Specifies that the position of the specified activity has been selected.
     *
     * @param activityId the id of the activity
     * @param position the position that has been occupied
     */
    public void takeAvailableSpot(Long activityId, String position) {
        try {
            Pair<Long, String> posTaken = Pair.of(activityId, position);
            Response res = matchingUtils.postRequest("/takeAvailableSpot", posTaken);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Specifies that a user has unenrolled for a position in an activity.
     *
     * @param activityId the id of the activity
     * @param position the position that has been unenrolled for
     */
    public void unenroll(Long activityId, String position) {
        try {
            Pair<Long, String> posTaken = Pair.of(activityId, position);
            Response res = matchingUtils.postRequest("/unenrollPosition", posTaken);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
