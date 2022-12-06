package com.example.micro.publishers;

import com.example.micro.domain.Matching;
import com.example.micro.utils.MatchingUtils;
import com.example.micro.utils.TimeSlot;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import lombok.Cleanup;
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
            @Cleanup
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
            @Cleanup
            Response res = matchingUtils.postRequest("/sendAvailableActivities/" + userId, timeSlots);
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
            @Cleanup
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
            matchingUtils.postRequest("/takeAvailableSpot", posTaken);
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
            matchingUtils.postRequest("/unenrollPosition", posTaken);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Checks if a matching is possible.
     *
     * @param matching The properties of the matching
     * @return true of false if it is possible
     */
    public Boolean check(Matching matching) {
        try {
            @Cleanup
            Response res = matchingUtils.getRequest("/check/" + matching.getUserId() + "/" + matching.getActivityId());
            Boolean check = res.readEntity(new GenericType<>() {});
            return check;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
