package com.example.activitymicroservice.utils;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.publishers.UserPublisher;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityContext {
    private final Activity activity;
    private final UserPublisher userPublisher;
    private final String position;
    private final String userId;

    /**
     * Constructor of ActivityContext class.
     *
     * @param activity an Activity object
     * @param userPublisher an UserPublisher object.
     * @param position a String object referring to a searched position by the User
     * @param userId a String object referring to the ID of the User
     */
    public ActivityContext(Activity activity, UserPublisher userPublisher, String position, String userId) {
        this.activity = activity;
        this.userPublisher = userPublisher;
        this.position = position;
        this.userId = userId;
    }


}