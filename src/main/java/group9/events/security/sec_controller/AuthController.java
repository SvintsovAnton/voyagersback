package group9.events.security.sec_controller;

import group9.events.domain.dto.ChangePasswordRequest;
import group9.events.domain.dto.ForgotPasswordRequest;
import group9.events.domain.dto.RestorePasswordRequest;
import group9.events.domain.dto.UserDto;
import group9.events.domain.entity.User;
import group9.events.exception_handler.exceptions.UserNotAuthenticatedException;
import group9.events.repository.UserRepository;
import group9.events.security.sec_dto.RefreshRequestDto;
import group9.events.security.sec_dto.TokenResponseDto;
import group9.events.security.sec_service.AuthService;
import group9.events.service.interfaces.UserService;
import group9.events.service.mapping.UserMappingService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService service;

    private UserRepository userRepository;

    private UserMappingService userMappingService;

    private UserService userService;

    public AuthController(AuthService service, UserRepository userRepository, UserMappingService userMappingService, UserService userService) {
        this.service = service;
        this.userRepository = userRepository;
        this.userMappingService = userMappingService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody User user, HttpServletResponse response) {
        try {
            TokenResponseDto tokenResponseDto = service.login(user);
            Cookie cookieAccess = new Cookie("Access-Token", tokenResponseDto.getAccessToken());
            cookieAccess.setPath("/");
            cookieAccess.setHttpOnly(true);
            response.addCookie(cookieAccess);

            Cookie cookieRefresh = new Cookie("Refresh-Token", tokenResponseDto.getRefreshToken());
            cookieRefresh.setPath("/");
            cookieRefresh.setHttpOnly(true);
            response.addCookie(cookieRefresh);
            return service.login(user);
        } catch (AuthException e) {
            throw new UserNotAuthenticatedException("user don´t authenticated");
        }
    }

    private void removeCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("Access-Token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        removeCookie(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile() {
        User user;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String userName = authentication.getName();
            user = userRepository.findByEmail(userName).orElse(null);
        } else {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        UserDto userDto = userMappingService.mapEntityToDto(user);
        return ResponseEntity.ok(userDto);
    }


    @PostMapping("/refresh")
    public TokenResponseDto getNewAccessToken(@RequestBody RefreshRequestDto request) {
        return service.getNewAccessToken(request.getRefreshToken());
    }

    @PutMapping("/reset-password")
    public UserDto resetPassword(@RequestParam("code") String token, @RequestBody RestorePasswordRequest request) {
        return userService.resetPassword(token, request);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<UserDto> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return ResponseEntity.ok(userService.forgotPassword(forgotPasswordRequest.getEmail()));

    }


}