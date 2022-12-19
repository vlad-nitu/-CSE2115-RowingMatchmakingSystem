package com.example.activitymicroservice.publishers;

import com.example.activitymicroservice.utils.ActivityUtils;
import lombok.Cleanup;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchingPublisher {

    private final transient ActivityUtils activityUtils;

    MatchingPublisher(ActivityUtils activityUtils) {
        this.activityUtils = activityUtils;
    }

    MatchingPublisher() {
        this.activityUtils = new ActivityUtils("http://localhost:8083/");
    }

    /**
     * Request matching to delete all matchings with the activity id.
     *
     * @param activityId the id of the activity
     * @return true if successfully deleted
     */
    public boolean deleteMatchingByActivityId(Long activityId) {
        try {
            @Cleanup
            Response res = activityUtils.getRequest("/deleteMatchingByActivityId/" + activityId);
            return res.getStatus() == 204;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
