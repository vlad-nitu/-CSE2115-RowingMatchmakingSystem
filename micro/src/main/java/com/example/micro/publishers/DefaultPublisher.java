package com.example.micro.publishers;

import com.example.micro.utils.MatchingUtils;
import lombok.Cleanup;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

@Service
public class DefaultPublisher {
    private final transient MatchingUtils matchingUtils;

    public DefaultPublisher(MatchingUtils matchingUtils) {
        this.matchingUtils = matchingUtils;
    }

    public DefaultPublisher() {
        this.matchingUtils = new MatchingUtils("http://localhost:8082/");
    }

    /**
     * Make a call from the Microservice API to the Custom controller (of 'example-microservice' component) in order
     * to check if the communication works fine with Authentication.
     *
     * @return - a String representing the user's greeting.
     */
    public String getGreetings() {
        try {
            @Cleanup
            Response res = matchingUtils.getRequest("/hello");
            String greeting = res.readEntity(new GenericType<>() {});
            return greeting;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }


    }
}