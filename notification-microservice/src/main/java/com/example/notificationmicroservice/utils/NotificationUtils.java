package com.example.notificationmicroservice.utils;

import lombok.AllArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@AllArgsConstructor
public class NotificationUtils {

    protected final transient String server;
    protected final transient ResteasyClient client;

    public NotificationUtils(String server) {
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
}
