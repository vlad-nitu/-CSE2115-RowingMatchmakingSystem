package com.example.micro.publishers;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Getter
@Service
@Generated
public class CollectionPublisher {
    private final transient ActivityPublisher activityPublisher;
    private final transient NotificationPublisher notificationPublisher;

}
