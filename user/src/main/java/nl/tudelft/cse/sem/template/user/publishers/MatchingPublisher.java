package nl.tudelft.cse.sem.template.user.publishers;

import lombok.Cleanup;
import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

import nl.tudelft.cse.sem.template.user.utils.Pair;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import nl.tudelft.cse.sem.template.user.utils.BaseMatching;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

@Service
public class MatchingPublisher {
    private final transient UserUtils userUtils;

    public MatchingPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public MatchingPublisher() {
        this.userUtils = new UserUtils("http://localhost:8083/");
    }

    public List<Pair<Long, String>> getAvailableActivities(String userId, List<TimeSlot> timeSlots) {
        try {
            Response res = userUtils.postRequest("/getAvailableActivities/" + userId, timeSlots);
            List<Pair<Long, String>> availableActivities = res.readEntity(new GenericType<>() {});
            return availableActivities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<Pair<Long, String>>();
        }
    }

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

    public BaseMatching decideMatch(String userId, String type, BaseMatching matching) {
        try {
            Response res = userUtils.postRequest("/decideMatch/" + userId + "/" + type, matching);
            BaseMatching matchResult = res.readEntity(new GenericType<>() {});
            return matchResult;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new BaseMatching();
        }
    }

    public BaseMatching chooseActivity(BaseMatching matching) {
        try {
            Response res = userUtils.postRequest("/chooseActivity", matching);
            BaseMatching matchResult = res.readEntity(new GenericType<>() {});
            return matchResult;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new BaseMatching();
        }
    }

    public Pair<String, Long> unenroll(Pair<String, Long> userIdActivityIdPair) {
        try {
            Response res = userUtils.postRequest("/unenroll", userIdActivityIdPair);
            Pair<String, Long> receivedPair = res.readEntity(new GenericType<>() {});
            return receivedPair;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Pair<String, Long>();
        }
    }

}
