package com.example.activitymicroservice.utils;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.message.internal.MsgTraceEvent;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class MatchingPublisher {
    private final transient String server;

    public MatchingPublisher(String server) {
        this.server = server;
    }

    public MatchingPublisher() {
        this.server = "http://localhost:8083/";
    }

    public Pair<Long, String> getAvailableSpot(){
        try{
            Client client = ClientBuilder.newClient(new ClientConfig());
            Response res = client.target(server).path("/takeAvailableSpot")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
            return res.readEntity(new GenericType<>() {});
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void takeTimeSlots(List<TimeSlot> timeSlotList) {
        try{
            Client client = ClientBuilder.newClient(new ClientConfig());
            client.target(server).path("/takeAvailableSpot")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(timeSlotList, APPLICATION_JSON), Response.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
