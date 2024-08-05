package group9.events.service.mapping;


import group9.events.domain.dto.EventCommentsDto;
import group9.events.domain.dto.UserDto;
import group9.events.domain.entity.EventComments;
import group9.events.domain.entity.User;
import group9.events.repository.GenderRepository;
import group9.events.repository.UserRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group9.events.domain.entity.Gender;

@Service
public class UserMappingService {

    @Autowired
    private GenderRepository genderRepository;

    @Autowired
    private UserRepository userRepository;


    public UserDto mapEntityToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setDateOfBirth(user.getDateOfBirth());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setPhoto(user.getPhoto());
        String gender2 = user.getGender().getGender();
        Gender gender = user.getGender();
        Gender gender3 = genderRepository.findById(gender.getId()).orElse(null);
        String title = gender3.getGender();
        userDto.setGender(title);
        return userDto;
    }

    public User mapDtoToEntity(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setPhoto(userDto.getPhoto());
        Gender gender = genderRepository.findByGender(userDto.getGender());
        user.setActive(userRepository.findByEmail(userDto.getEmail()).orElse(null).getActive());
        user.setRoles(userRepository.findByEmail(userDto.getEmail()).orElse(null).getRoles());
        user.setPassword(userRepository.findByEmail(userDto.getEmail()).orElse(null).getPassword());
        user.setId(userRepository.findByEmail(userDto.getEmail()).orElse(null).getId());
        user.setGender(gender);

        return user;
    }
}