package group9.events.service.interfaces;

import group9.events.domain.entity.User;

public interface EmailService {

    void sendConfirmationEmail(User user);

    void sendEmailWhenPasswordIsForgotten(String email);


}