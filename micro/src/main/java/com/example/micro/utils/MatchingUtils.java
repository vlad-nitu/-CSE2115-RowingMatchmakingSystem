package com.example.micro.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;


public class MatchingUtils {

    protected final transient String server;

    protected final transient  ResteasyClient client;

    public MatchingUtils(String server) {
        this.server = server;
        this.client = new ResteasyClientBuilder().build();
    }

    /**
     * This method is a generic method for a get request.
     *
     * @param path The url/path needed for the specific request.
     * @return the response of the server.
     */
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

    /**
     * This method is a generic method for a post request.
     *
     * @param <T>  Allows this method to use a generic data type, instead of being specific
     * @param path The url/path needed for the specific request
     * @param data The data that you want to post on the server
     * @return response
     */
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
