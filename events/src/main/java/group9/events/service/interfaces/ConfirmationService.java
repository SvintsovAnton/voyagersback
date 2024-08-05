package group9.events.service.interfaces;

import group9.events.domain.entity.User;

public interface ConfirmationService {

    String generateConfirmationCode(User user);

    User getUserByConfirmationCode(String code);

    String validateConfirmationCode(String token);
}