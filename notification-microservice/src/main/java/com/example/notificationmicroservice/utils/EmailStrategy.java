package com.example.notificationmicroservice.utils;

import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.publishers.UserPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

//Primary tag specifies to the Notification Controller that this
//is the default strategy of handling notifications
@Primary
@Component
public class EmailStrategy implements NotificationStrategy {

    private final transient JavaMailSender mailSender;
    private final transient UserPublisher userPublisher;

    @Autowired
    public EmailStrategy(UserPublisher userPublisher, JavaMailSender mailSender) {
        this.userPublisher = userPublisher;
        this.mailSender = mailSender;
    }

    /** Handles the notification by trying to send an email to the user.
     *
     * @param notification notification that is to be sent to user by email
     * @return weather it was successfully sent
     */
    @Override
    public boolean handleNotification(Notification notification) {
        String subject;
        if (notification.getType().equals("notifyOwner")) {
            subject = "A new request";
        } else {
            subject = "Accepted for activity";
        }

        String toEmail = "laimonas.cao@gmail.com";
        String body = notification.buildMessage();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sem33aservice@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    /** A method that returns failure text for the strategy.
     *
     * @return a failure message if handleNotification fails
     */
    @Override
    public String getFailureMessage() {
        return "Failed to send email";
    }
}
