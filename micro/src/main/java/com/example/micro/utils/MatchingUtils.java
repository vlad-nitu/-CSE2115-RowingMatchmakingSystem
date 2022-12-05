package com.example.micro.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

public class MatchingUtils {

    protected String server;

    protected Client client;

    public MatchingUtils(String server) {
        this.server = server;
        client = ClientBuilder.newClient();
    }

    public Response getRequest(String path) throws Exception {
        try {
            Response res = client.target(server).path(path)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(Response.class);
            if ((res.getStatus() / 100) != 2) {
                throw new Exception("Bad request");
            }
            return res;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public <T> Response postRequest(String path, T data) throws Exception {
        try {
            Response res = client.target(server).path(path)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(data, APPLICATION_JSON), Response.class);
            if ((res.getStatus() / 100) != 2) {
                throw new Exception("Bad request");
            }
            return res;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


}
