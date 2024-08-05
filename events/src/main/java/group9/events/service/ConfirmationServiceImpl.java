package group9.events.service;

import group9.events.domain.entity.ConfirmationCode;
import group9.events.domain.entity.User;
import group9.events.exception_handler.ConfirmationFailedException;
import group9.events.repository.ConfirmationCodeRepository;
import group9.events.service.interfaces.ConfirmationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ConfirmationServiceImpl implements ConfirmationService {

    private final ConfirmationCodeRepository repository;
    private final Map<String, String> tokenStorage = new HashMap<>();

    public ConfirmationServiceImpl(ConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public String generateConfirmationCode(User user) {
        LocalDateTime expired = LocalDateTime.now().plusDays(10);
        String code = UUID.randomUUID().toString();
        ConfirmationCode entity = new ConfirmationCode(code, expired, user);
        repository.save(entity);
        tokenStorage.put(code, user.getEmail());
        return code;
    }

    @Override
    public User getUserByConfirmationCode(String code) {
        ConfirmationCode entity = repository.findByCode(code).orElse(null);
        if (entity == null) {
            throw new ConfirmationFailedException("Confirmation code not found");
        }
        if (LocalDateTime.now().isAfter(entity.getExpired())) {
            throw new ConfirmationFailedException("Confirmation code expired");
        }
        return entity.getUser();
    }

    @Override
    public String validateConfirmationCode(String token) {
        if (tokenStorage.containsKey(token)) {
            return tokenStorage.get(token);
        }
        ConfirmationCode codeEntity = repository.findByCode(token).orElse(null);
        if (codeEntity == null) {
            throw new RuntimeException("Invalid token");
        }

        if (LocalDateTime.now().isAfter(codeEntity.getExpired())) {
            throw new RuntimeException("Token expired");
        }


        tokenStorage.put(token, codeEntity.getUser().getEmail());
        return codeEntity.getUser().getEmail();
    }
    }
