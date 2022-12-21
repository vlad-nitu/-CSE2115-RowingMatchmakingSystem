package nl.tudelft.cse.sem.template.user.publishers;

import lombok.Cleanup;
import lombok.Generated;
import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

import nl.tudelft.cse.sem.template.user.utils.Pair;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import nl.tudelft.cse.sem.template.user.utils.BaseMatching;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

@Service
@Generated
public class MatchingPublisher {
    private final transient UserUtils userUtils;

    public MatchingPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public MatchingPublisher() {
        this.userUtils = new UserUtils("http://localhost:8083/");
    }

    /**
     * Request available activities based on the userId and the timeSlot where the user
     * is available.
     *
     * @param userId - the id of the user requesting to see the available activities
     * @param timeSlots - the timeslots where the user is available for an activity
     * @return a list of pairs which give the combination of activity id and time slot of the activity
     */
    public List<Pair<Long, String>> getAvailableActivities(String userId, Set<TimeSlot> timeSlots) {
        try {
            @Cleanup
            Response res = userUtils.postRequest("/getAvailableActivities/" + userId, timeSlots);
            List<Pair<Long, String>> availableActivities = res.readEntity(new GenericType<>() {});
            return availableActivities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<Pair<Long, String>>();
        }
    }

    /**
     * Request the activities where the user is participating in.
     *
     * @param userId the id of the user for which the request is made
     * @return list of activity id's
     */
    public List<Long> getUserActivities(String userId) {
        try {
            @Cleanup
            Response res = userUtils.getRequest("/getUserActivities/" + userId);
            List<Long> activityIds = res.readEntity(new GenericType<>() {});
            return activityIds;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<Long>();
        }
    }

    /**
     * Decides if a match is accepted or declined.
     *
     * @param userId the id of the owner who is deciding
     * @param type accept or decline, based on the choice of the owner of the activity
     * @param matching the matching that is reviewed
     * @return a copy of the result of the matching after the decision
     */
    public BaseMatching decideMatch(String userId, String type, BaseMatching matching) {
        try {
            @Cleanup
            Response res = userUtils.postRequest("/decideMatch/" + userId + "/" + type, matching);
            BaseMatching matchResult = res.readEntity(new GenericType<>() {});
            return matchResult;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new BaseMatching();
        }
    }

    /**
     * Sends the activity the user wants to participate in.
     *
     * @param matching a matching with the chosen activity id, user id, pending as true
     *                 (as owner still needs to accept) and the timeSlot of the activity
     * @return the copy of the saved match, none if an exception is thrown
     */
    public BaseMatching chooseActivity(BaseMatching matching) throws Exception {
        Response res = userUtils.postRequest("/chooseActivity", matching);
        return res.getStatus() == 200 ? res.readEntity(new GenericType<>() {}) : null;
    }

    /**
     * Sends a request to unenroll from an activity the user was participating in.
     *
     * @param userIdActivityIdPair A pair which is the combination of the activity id from the activity
     *                             where the user wants to unenroll from and the user id itself
     * @return a copy of the pair when unenrollment was succesful
     */
    public Pair<String, Long> unenroll(Pair<String, Long> userIdActivityIdPair) {
        try {
            @Cleanup
            Response res = userUtils.postRequest("/unenroll", userIdActivityIdPair);
            Pair<String, Long> receivedPair = res.readEntity(new GenericType<>() {});
            return receivedPair;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Pair<String, Long>();
        }
    }

}
