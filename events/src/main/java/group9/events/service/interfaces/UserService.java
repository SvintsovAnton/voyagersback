package group9.events.service.interfaces;


import group9.events.domain.dto.ChangePasswordRequest;
import group9.events.domain.dto.RestorePasswordRequest;
import group9.events.domain.dto.UserDto;
import group9.events.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDto register(User user);


    List<UserDto> getAllUsers();

    UserDto transferAdminRole(Long id);

    UserDto blockUser(Long id);

    void registrationConfirm(String code);

    UserDto changePassword(String oldPassword,String newPassword);

    UserDto resetPassword(String token, RestorePasswordRequest request);

    UserDto forgotPassword(String email);

    UserDto addPhoto(String urlPhoto);



}
