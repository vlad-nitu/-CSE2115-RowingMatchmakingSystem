package com.example.activitymicroservice.publishers;

import com.example.activitymicroservice.utils.ActivityUtils;
import lombok.Cleanup;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserPublisher {

    private final transient ActivityUtils activityUtils;

    UserPublisher(ActivityUtils activityUtils) {
        this.activityUtils = activityUtils;
    }

    UserPublisher() {
        this.activityUtils = new ActivityUtils("http://localhost:8085/");
    }

    /**
     * Request the competitiveness of the user.
     *
     * @param userId the id of the user
     * @return the competitiveness of the user
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
     * Request the gender of the user.
     *
     * @param userId the id of the user
     * @return the gender of the user
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
     * Request the organisation of the user.
     *
     * @param userId the id of the user
     * @return the organisation of the user
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
     * Request the certificate of the user.
     *
     * @param userId the id of the user
     * @return the certificate of the user
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
     * Request the positions the user can fill in.
     *
     * @param userId the id of the user
     * @return a set with the positions the user can fill in
     */
    public List<String> getPositions(String userId) {
        try {
            @Cleanup
            Response res = activityUtils.getRequest("/sendPositions/" + userId);
            List<String> positions = res.readEntity(new GenericType<>() {});
            return  positions;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            List<String> emptyList = new ArrayList<>();
            return emptyList;
        }
    }
}
