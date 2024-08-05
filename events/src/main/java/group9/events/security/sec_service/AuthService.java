package group9.events.security.sec_service;

import group9.events.domain.dto.UserDto;
import group9.events.domain.entity.User;
import group9.events.exception_handler.ConfirmationFailedException;

import group9.events.exception_handler.exceptions.InvalidPasswordException;
import group9.events.exception_handler.exceptions.UserNotFoundException;
import group9.events.repository.UserRepository;
import group9.events.security.sec_dto.TokenResponseDto;
import group9.events.service.interfaces.UserService;
import group9.events.service.mapping.UserMappingService;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private UserService userService;
    private TokenService tokenService;
    private Map<String, String> refreshStorage;
    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private UserMappingService userMappingService;


    public AuthService(UserService userService, TokenService tokenService, Map<String, String> refreshStorage, BCryptPasswordEncoder passwordEncoder, UserRepository userRepository, UserMappingService userMappingService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.refreshStorage = refreshStorage;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userMappingService = userMappingService;
    }

    public TokenResponseDto login(User inboundUser) throws AuthException {
        String username = inboundUser.getUsername();
        User foundUser = (User) userService.loadUserByUsername(username);

        if (!foundUser.getActive()) {
            throw new ConfirmationFailedException("your registration is not confirmed or the user is blocked");
        }

        if (passwordEncoder.matches(inboundUser.getPassword(), foundUser.getPassword())) {
            String accessToken = tokenService.generateAccessToken(foundUser);
            String refreshToken = tokenService.generateRefreshToken(foundUser);
            refreshStorage.put(username, refreshToken);
            return new TokenResponseDto(accessToken, refreshToken);
        } else {
            throw new InvalidPasswordException("Password is incorrect");
        }
    }

    public TokenResponseDto getNewAccessToken(String inboundRefreshToken) {
        Claims refreshClaims = tokenService.getRefreshClaims(inboundRefreshToken);
        String username = refreshClaims.getSubject();
        String savedRefreshToken = refreshStorage.get(username);

        if (savedRefreshToken != null && savedRefreshToken.equals(inboundRefreshToken)) {
            User user = (User) userService.loadUserByUsername(username);
            String accessToken = tokenService.generateAccessToken(user);
            return new TokenResponseDto(accessToken, null);
        } else {
            return new TokenResponseDto(null, null);
        }
    }


    public UserDto getUserProfile(String token){
        if (tokenService.validateAccessToken(token)){
            Claims claims = tokenService.getAccessClaims(token);
            String email = claims.getSubject();
            User user = userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User don´t found"));
            if(user!=null){
               UserDto userDto= userMappingService.mapEntityToDto(user);
               return userDto;
            }
        }
        throw new UserNotFoundException("User don´t found");
    }



}