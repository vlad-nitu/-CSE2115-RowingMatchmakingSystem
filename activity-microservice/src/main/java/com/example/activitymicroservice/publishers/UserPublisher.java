package com.example.activitymicroservice.publishers;

import com.example.activitymicroservice.utils.ActivityUtils;
import lombok.Cleanup;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

public class UserPublisher {

    private final transient ActivityUtils activityUtils;

    UserPublisher(ActivityUtils activityUtils) {
        this.activityUtils = activityUtils;
    }

    UserPublisher() {
        this.activityUtils = new ActivityUtils("http://localhost:8083/");
    }

    /**
     * Request the competitiveness of the activity.
     *
     * @param userId the id of the user
     * @return the competitiveness of the activity
     */
    public boolean getCompetitiveness(String userId) {
        try {
            @Cleanup
            Response res = activityUtils.getRequest("/sendCompetitiveness/" + userId);
            boolean competitiveness = res.readEntity(new GenericType<>() {});
            return competitiveness;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Request the gender required for the activity.
     *
     * @param userId the id of the user
     * @return the gender required for the activity
     */
    public  Character getGender(String userId) {
        try {
            @Cleanup
            Response res = activityUtils.getRequest("/sendGender/" + userId);
            Character gender = res.readEntity(new GenericType<>() {});
            return gender;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 'N';
        }
    }

    /**
     * Request the organisation required for the activity.
     *
     * @param userId the id of the user
     * @return the organisation required for the activity
     */
    public String getOrganisation(String userId) {
        try {
            @Cleanup
            Response res = activityUtils.getRequest("/sendOrganization/" + userId);
            String organisation = res.readEntity(new GenericType<>() {});
            return  organisation;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * Request the certificate required for the activity.
     *
     * @param userId the id of the user
     * @return the certificate required for the activity
     */
    public String getCertificate(String userId) {
        try {
            @Cleanup
            Response res = activityUtils.getRequest("/sendCertificate/" + userId);
            String certificate = res.readEntity(new GenericType<>() {});
            return  certificate;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * Request the positions available at the activity.
     *
     * @param userId the id of the user
     * @return a set with the available positions
     */
    public Set<String> getPositions(String userId) {
        try {
            @Cleanup
            Response res = activityUtils.getRequest("/sendPositions/" + userId);
            Set<String> positions = res.readEntity(new GenericType<>() {});
            return  positions;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Set<String> emptySet = Collections.<String>emptySet();
            return emptySet;
        }
    }
}
