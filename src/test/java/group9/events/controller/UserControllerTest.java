package group9.events.controller;

import group9.events.domain.dto.UserDto;
import group9.events.domain.entity.*;
import group9.events.exception_handler.exceptions.UserNotFoundException;
import group9.events.repository.*;
import group9.events.security.sec_dto.TokenResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("dev")
class UserControllerTest {

    @LocalServerPort
    private int port;


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenderRepository genderRepository;


    private TestRestTemplate template;
    private HttpHeaders headers;

    private String adminAccessToken;
    private String userAccessToken;

    private String userAccessToken2;



    private final String TEST_ADMIN_NAME = "Test Admin";
    private final String TEST_USER_NAME = "Test User";
    private final String LAST_NAME_FOR_ALLE = "Test";
    private final String TEST_PASSWORD = "TestPasword!";
    private final String ROLE_ADMIN_TITLE = "ROLE_ADMIN";
    private final String ROLE_USER_TITLE = "ROLE_USER";
    private final String EMAIL_FOR_USER = "usertest1@test.com";
    private final String EMAIL_FOR_USER2 = "usertest2@test.com";
    private final String EMAIL_FOR_ADMIN = "admintest1@test.com";
    private final String EMAIL_FOR_USER_AUTHENTICATION = "userauthtest@test.com";
    private final String GENDER_MALE="Male";
    private final String GENDER_FEMALE = "Female";
    private final Date DATE_OF_BIRTH;
    {
        try {
            DATE_OF_BIRTH = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private final String PHONE ="+123456789";
    private final String PFOTO = "example.com";





    private final String BEARER_PREFIX= "Bearer ";


    private final String URL_PREFIX = "http://localhost:";
    private final String AUTH_RESOURCE_NAME="/api/auth";
    private final String USER_RESOURCE_NAME = "/api/users";
    private final String LOGIN_ENDPOINT = "/login";

    private final String ROLE_ENDPOINT = "/role";

    private final String BLOCK_ENDPOINT="/block";

    private final String SLASH ="/";

    private final String REGISTER_ENDPOINT ="/register";



    static Long USER_ID;
    static Long USER_ID2;


   static User user;

   static User admin;

   static User user2;

   static Set<Role> roleForAdmin;
   static Set<Role> roleForUser;
   static Set<Role> roleForUser2;


    @BeforeEach
    public void setUp(){

        template = new TestRestTemplate();
        headers= new HttpHeaders();


        BCryptPasswordEncoder encoder = null;
        Role roleAdmin;
        Role roleUser = null;

         admin = userRepository.findByEmail(EMAIL_FOR_ADMIN).orElse(null);
         user = userRepository.findByEmail(EMAIL_FOR_USER).orElse(null);
         user2 =userRepository.findByEmail(EMAIL_FOR_USER2).orElse(null);


        if (admin == null) {
            encoder = new BCryptPasswordEncoder();
            roleAdmin = roleRepository.findByTitle(ROLE_ADMIN_TITLE).orElseThrow(
                    () -> new RuntimeException("Role Admin is missing in the database")
            );
            roleUser = roleRepository.findByTitle(ROLE_USER_TITLE).orElseThrow(
                    () -> new RuntimeException("Role User is missing in the database")
            );
            admin = new User();
            admin.setLastName(TEST_ADMIN_NAME);
            admin.setFirstName(LAST_NAME_FOR_ALLE);
            admin.setDateOfBirth(DATE_OF_BIRTH);
            admin.setEmail(EMAIL_FOR_ADMIN);
            admin.setPassword(encoder.encode(TEST_PASSWORD));
            admin.setPhone(PHONE);
            admin.setPhone(PFOTO);
            roleForAdmin =Set.of(roleAdmin, roleUser);
            admin.setRoles(roleForAdmin);
            admin.setGender(genderRepository.findByGender(GENDER_MALE));
            admin.setActive(true);
            userRepository.save(admin);
        }

        if (user == null) {
            encoder = encoder == null ? new BCryptPasswordEncoder() : encoder;
            roleUser = roleUser == null ? roleRepository.findByTitle(ROLE_USER_TITLE).orElseThrow(
                    () -> new RuntimeException("Role User is missing in the database")
            ) : roleUser;
            user = new User();
            user.setLastName(TEST_USER_NAME);
            user.setFirstName(LAST_NAME_FOR_ALLE);
            user.setDateOfBirth(DATE_OF_BIRTH);
            user.setEmail(EMAIL_FOR_USER);
            user.setPassword(encoder.encode(TEST_PASSWORD));
            user.setPhone(PHONE);
            user.setPhoto(PFOTO);
            roleForUser = Set.of(roleUser);
            user.setRoles(roleForUser);
            user.setGender(genderRepository.findByGender(GENDER_FEMALE));
            user.setActive(true);
            userRepository.save(user);
        }

        if (user2 == null) {
            encoder = encoder == null ? new BCryptPasswordEncoder() : encoder;
            roleUser = roleUser == null ? roleRepository.findByTitle(ROLE_USER_TITLE).orElseThrow(
                    () -> new RuntimeException("Role User is missing in the database")
            ) : roleUser;
            user2 = new User();
            user2.setLastName(TEST_USER_NAME);
            user2.setFirstName(LAST_NAME_FOR_ALLE);
            user2.setDateOfBirth(DATE_OF_BIRTH);
            user2.setEmail(EMAIL_FOR_USER2);
            user2.setPassword(encoder.encode(TEST_PASSWORD));
            user2.setPhone(PHONE);
            user2.setPhoto(PFOTO);
            roleForUser2 = Set.of(roleUser);
            user2.setRoles(roleForUser2);
            user2.setGender(genderRepository.findByGender(GENDER_FEMALE));
            user2.setActive(true);
            userRepository.save(user2);
        }

        USER_ID=userRepository.findByEmail(EMAIL_FOR_USER).get().getId();
        USER_ID2=userRepository.findByEmail(EMAIL_FOR_USER2).get().getId();

        admin.setPassword(TEST_PASSWORD);
        admin.setRoles(null);
        user.setPassword(TEST_PASSWORD);
        user.setRoles(null);
        user2.setPassword(TEST_PASSWORD);
        user2.setRoles(null);


        String url = URL_PREFIX + port + AUTH_RESOURCE_NAME + LOGIN_ENDPOINT;

        HttpEntity<User> request = new HttpEntity<>(admin, headers);
        ResponseEntity<TokenResponseDto> response = template.exchange(url, HttpMethod.POST, request, TokenResponseDto.class);

        assertTrue(response.hasBody(), "Auth response body is empty");

        adminAccessToken = BEARER_PREFIX + response.getBody().getAccessToken();

        request = new HttpEntity<>(user, headers);
        response = template.exchange(url, HttpMethod.POST, request, TokenResponseDto.class);

        assertTrue(response.hasBody(), "Auth response body is empty");
        userAccessToken = BEARER_PREFIX + response.getBody().getAccessToken();

        request = new HttpEntity<>(user2, headers);
        response = template.exchange(url, HttpMethod.POST, request, TokenResponseDto.class);

        assertTrue(response.hasBody(), "Auth response body is empty");
        userAccessToken2 = BEARER_PREFIX + response.getBody().getAccessToken();
    }


    @Test
    @Order(1)
    void positiveGetAllUsersByAdmin() {
        String url = URL_PREFIX+port+USER_RESOURCE_NAME;
        headers.set("Authorization", adminAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<UserDto[]> response =  template.exchange(url,HttpMethod.GET,request,UserDto[].class);
        List<UserDto> listOfUserDto = Arrays.asList(response.getBody());
        assertFalse(listOfUserDto.isEmpty());
    }


    @Test
    @Order(2)
    void negativeGetAllUsersByUser(){
        String url = URL_PREFIX+port+USER_RESOURCE_NAME;
        headers.set("Authorization", userAccessToken2);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response =  template.exchange(url,HttpMethod.GET,request,Void.class);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }

    @Test
    @Order(3)
    void negativeTransferAdminRoleByUser(){
        String url = URL_PREFIX+port+USER_RESOURCE_NAME+ROLE_ENDPOINT+SLASH+USER_ID;
        headers.set("Authorization", userAccessToken2);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response =  template.exchange(url,HttpMethod.GET,request,Void.class);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }

    @Test
    @Order(4)
    void positiveTransferAdminRoleByUser(){
        String url = URL_PREFIX+port+USER_RESOURCE_NAME+ROLE_ENDPOINT+SLASH+USER_ID;
        headers.set("Authorization", adminAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<UserDto> response =  template.exchange(url,HttpMethod.PUT,request,UserDto.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        Set<Role> setOfRoles = userRepository.findByEmail(EMAIL_FOR_USER).get().getRoles();
        Long count = setOfRoles.stream().filter(x->x.getTitle().equals("ROLE_ADMIN")).count();
        assertEquals(1L,count);

        Role roleUserForUpdate = roleRepository.findByTitle("ROLE_USER").orElseThrow(
                () -> new RuntimeException("Role User is missing in the database"));
        Set<Role> roleForUserForUpdate = Set.of(roleUserForUpdate);
        User userUpdate = userRepository.findByEmail(EMAIL_FOR_USER).orElseThrow(()->new UserNotFoundException("User not found"));
        userUpdate.setRoles(roleForUserForUpdate);
        userRepository.save(userUpdate);

    }


    @Test
    @Order(5)
    void negativeBlockUserByUser(){
        String url = URL_PREFIX+port+USER_RESOURCE_NAME+BLOCK_ENDPOINT+SLASH+USER_ID;
        headers.set("Authorization", userAccessToken2);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<UserDto> response = template.exchange(url,HttpMethod.PUT,request, UserDto.class);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }

    @Test
    @Order(6)
    void blockUser() {
        String url = URL_PREFIX+port+USER_RESOURCE_NAME+BLOCK_ENDPOINT+SLASH+USER_ID;
        headers.set("Authorization", adminAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<UserDto> response = template.exchange(url,HttpMethod.PUT,request, UserDto.class);
        assertTrue(response.getBody().getEmail().equals(EMAIL_FOR_USER));
        assertEquals(userRepository.findById(USER_ID).orElse(null).getActive(),false);

        User userUpdate = userRepository.findByEmail(EMAIL_FOR_USER).orElseThrow(()->new UserNotFoundException("User not found"));
        userUpdate.setActive(true);
        userRepository.save(userUpdate);
    }

    @Test
    @Order(7)
    void positiveCreateNewUserWithCheckAuthorisation()
    {
        String url =URL_PREFIX+port+USER_RESOURCE_NAME+REGISTER_ENDPOINT;
        User user = new User();
        user.setFirstName(TEST_USER_NAME);
        user.setLastName(LAST_NAME_FOR_ALLE);
        user.setDateOfBirth(DATE_OF_BIRTH);
        user.setEmail(EMAIL_FOR_USER_AUTHENTICATION);
        user.setPhone(PHONE);
        user.setPassword(TEST_PASSWORD);
        user.setPhoto(PFOTO);
        user.setGender(genderRepository.findByGender(GENDER_FEMALE));

    }

}