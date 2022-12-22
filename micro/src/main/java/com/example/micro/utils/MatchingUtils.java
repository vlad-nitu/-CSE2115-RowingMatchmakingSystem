package com.example.micro.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.example.micro.authentication.AuthManager;
import lombok.AllArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.springframework.security.core.context.SecurityContextHolder;


@AllArgsConstructor
public class MatchingUtils {

    protected final transient String server;
    protected final transient  ResteasyClient client;

    public MatchingUtils(String server) {
        this.server = server;
        this.client = new ResteasyClientBuilder().build();
    }

    public MatchingUtils() {
        this.server = "";
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
            Object token = SecurityContextHolder.getContext().getAuthentication().getCredentials();
            Response res = client.target(server).path(path)
                    .request(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .accept(APPLICATION_JSON)
                    .get(Response.class);
            return res;
        } catch (Exception e) {
            throw new Exception("Bad request");
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
            Object token = SecurityContextHolder.getContext().getAuthentication().getCredentials();
            Response res = client.target(server).path(path)
                    .request(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(data, APPLICATION_JSON), Response.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Bad request");
        }
    }


}
