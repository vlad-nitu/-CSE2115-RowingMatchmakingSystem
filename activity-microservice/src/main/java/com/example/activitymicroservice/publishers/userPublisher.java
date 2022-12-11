package com.example.activitymicroservice.publishers;

import com.example.activitymicroservice.utils.activityUtil;
import org.checkerframework.checker.units.qual.C;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class userPublisher {

    private final transient activityUtil activityUtils;

    userPublisher(activityUtil activityUtils){
        this.activityUtils = activityUtils;
    }

    userPublisher(){
        this.activityUtils = new activityUtil("http://localhost:8083/");
    }

    public boolean getCompetitiveness(String userId){
        try {
            Response res = activityUtils.getRequest("/sendCompetitiveness/" + userId);
            boolean competitiveness = res.readEntity(new GenericType<>() {});
            return competitiveness;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public  Character getGender(String userId){
        try {
            Response res = activityUtils.getRequest("/sendGender/" + userId);
            Character gender = res.readEntity(new GenericType<>() {});
            return gender;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 'N';
        }
    }
}
