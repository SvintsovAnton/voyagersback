package group9.events.controller;

import group9.events.domain.dto.ChangePasswordRequest;
import group9.events.domain.dto.UserDto;
import group9.events.domain.entity.User;
import group9.events.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody User user) {
        return service.register(user);
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @PutMapping("/role/{user_id}")
    @Transactional
    public UserDto transferAdminRole(@PathVariable Long user_id) {
        return service.transferAdminRole(user_id);
    }

    @PutMapping("/block/{user_id}")
    public UserDto blockUser(@PathVariable Long user_id) {
        return service.blockUser(user_id);
    }


    @PutMapping("/changepass")
    public UserDto changePassword(@RequestBody ChangePasswordRequest request) {
        return service.changePassword(request.getOldPassword(), request.getNewPassword());
    }

    @PostMapping("/addphoto/{url}")
    public UserDto addPhoto(@PathVariable String url)
    {return service.addPhoto(url);}
}
