package com.example.activitymicroservice.publishers;

import com.example.activitymicroservice.utils.ActivityUtils;
import com.example.activitymicroservice.utils.TimeSlot;
import lombok.Cleanup;
import lombok.Generated;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Generated
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
     * Request the time slots of the user.
     *
     * @param userId the id of the user
     * @return the certificate of the user
     */
    public List<TimeSlot> getTimeslots(String userId) {
        try {
            @Cleanup
            Response res = activityUtils.getRequest("/sendTimeSlots/" + userId);
            List<TimeSlot> timeSlots = res.readEntity(new GenericType<>() {});
            return  timeSlots;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Request the positions the user can fill in.
     *
     * @param userId the id of the user
     * @return a set with the positions the user can fill in
     */
    public Set<String> getPositions(String userId) {
        try {
            @Cleanup
            Response res = activityUtils.getRequest("/sendPositions/" + userId);
            Set<String> positions = res.readEntity(new GenericType<>() {});
            return  positions;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Set<String> emptyList = new HashSet<>();
            return emptyList;
        }
    }
}
